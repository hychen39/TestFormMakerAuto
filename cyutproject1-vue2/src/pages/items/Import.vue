<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("items.import.Import Items") }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
      </v-app-bar>

      <v-card-text v-if="false">
        <h3 style="color: red">
          {{ message }}
        </h3>
      </v-card-text>
      <v-card-text v-if="false">
        <v-row>
          <v-col>
            <v-card outlined>
              <v-list-item>
                <v-list-item-content>
                  <div class="text-h5 mb-1">
                    {{ $t("items.import.Uploads") }}
                  </div>
                </v-list-item-content>

                <v-list-item-avatar size="60" color="primary">
                  <span class="white--text text-h6">
                    {{ itemUrl.length }}
                  </span>
                </v-list-item-avatar>
              </v-list-item>
            </v-card>
          </v-col>
          <v-col>
            <v-card outlined>
              <v-list-item>
                <v-list-item-content>
                  <div class="text-h5 mb-1">
                    {{ $t("items.import.Included Images") }}
                  </div>
                </v-list-item-content>

                <v-list-item-avatar size="60" color="primary">
                  <span class="white--text text-h6">
                    {{ count }}
                  </span>
                </v-list-item-avatar>
              </v-list-item>
            </v-card>
          </v-col>
          <v-col>
            <v-card outlined>
              <v-list-item>
                <v-list-item-content>
                  <div class="text-h5 mb-1">
                    {{ $t("items.import.Upload Images") }}
                  </div>
                </v-list-item-content>

                <v-list-item-avatar size="60" color="primary">
                  <span class="white--text text-h6">
                    {{ imageUrl.length }}
                  </span>
                </v-list-item-avatar>
              </v-list-item>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-text
        v-if="mapFiles"
        class="overflow-auto"
        style="height: calc(100vh - 122px)"
      >
        <v-row>
          <v-col>
            <v-row>
              <v-col class="flex-grow-0">
                <v-btn
                  color="primary"
                  class="mb-3"
                  @click="
                    mapFiles = null;
                    files = [];
                  "
                >
                  <v-icon> mdi-delete-forever </v-icon>
                  {{ $t("items.import.Delete All") }}
                </v-btn>
              </v-col>
              <v-col>
                <v-switch
                  class="mt-0"
                  v-model="showAllItems"
                  :label="$t('items.import.Show All Items')"
                >
                </v-switch>
              </v-col>
            </v-row>
          </v-col>
          <v-spacer></v-spacer>
          <v-col class="text-right">
            <v-btn
              color="primary"
              :disabled="!canImport"
              @click="onImporting"
              :loading="importLoading"
            >
              <v-icon>mdi-import</v-icon>
              {{ $t("items.import.Import Items") }}
            </v-btn>
          </v-col>
        </v-row>

        <v-row v-if="mdFiles">
          <template v-for="(files, directory) in mdFiles">
            <v-subheader :key="`directory_header_${directory}`">
              <span class="font-weight-bold">{{ directory }}</span>
              <span class="caption ml-3"> (Files: {{ files.length }}) </span>
            </v-subheader>
            <v-col cols="12" :key="`directory_table_${directory}`">
              <v-data-table
                :headers="uploadHeaders"
                :items="getItems(files)"
                disable-pagination
                hide-default-footer
              >
                <template v-slot:item.filename="{ item }">
                  {{ item.filename }}
                </template>

                <template v-slot:item.content_format="{ item }">
                  <import-format-status :errors="item.content_format" />
                </template>
                <template v-slot:item.images_valid="{ item }">
                  <import-format-status :errors="item.images_valid" />
                </template>
                <template v-slot:item.upload_status="{ item }">
                  <div v-if="item.success !== undefined">
                    <import-format-status
                      :errors="item.success ? [] : [item.message]"
                    />
                  </div>
                </template>
                <template v-slot:no-data>
                  {{ $t("items.import.All Items Passed Validation") }}
                </template>
              </v-data-table>
            </v-col>
          </template>
        </v-row>
      </v-card-text>
      <v-card-text v-else>
        <vue-dropzone
          ref="dropzone"
          id="dropzone"
          :options="dropzoneOptions"
          @vdropzone-file-added="fileAdded"
        >
        </vue-dropzone>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import debounce from "lodash.debounce";
import axios from "@/services/axios";
import { create, getItemByQuestionNumber, update } from "@/services/ItemApi";
import { combinations, sqrt, round } from "mathjs";
import collect from "collect.js";
import vueDropzone from "vue2-dropzone";
import "vue2-dropzone/dist/vue2Dropzone.min.css";
import ImportFormatStatus from "@/components/items/ImportFormatStatus.vue";

export default {
  name: "Import",
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  components: {
    ImportFormatStatus,
    vueDropzone,
  },
  data() {
    return {
      files: [],
      mapFiles: null,
      itemUrl: [],
      imageUrl: [],
      md: [],
      count: 0,
      message: "",
      importLoading: false,
      dropzoneOptions: {
        autoProcessQueue: false,
        acceptedFiles: ".md,.png,.jpg,.jpeg",
        url: "https://httpbin.org/post",
      },
      reload: false,
      showAllItems: false,
    };
  },
  computed: {
    uploadHeaders() {
      return [
        {
          text: this.$t("items.import.Upload File"),
          value: "filename",
        },
        {
          text: this.$t("items.import.Valid Images"),
          value: "images_valid",
        },
        {
          text: this.$t("items.import.Valid Content Format"),
          value: "content_format",
        },
        {
          text: this.$t("items.import.Upload Status"),
          value: "upload_status",
        },
      ];
    },
    canImport() {
      if (this.mdFiles) {
        return collect(this.mdFiles)
          .values()
          .collapse()
          .filter(
            (file) =>
              file.content_format.length > 0 || file.images_valid.length > 0
          )
          .isEmpty();
      }
      return false;
    },
    mdFiles() {
      this.reload;
      if (this.mapFiles) {
        return collect(this.mapFiles)
          .map((files) => {
            return collect(files)
              .filter((value) => value.type === "md")
              .values()
              .map((file) => {
                return {
                  ...file,
                  content_format: this.validContent(file),
                  images_valid: this.validImages(file),
                  success: file.success,
                  message: file.message,
                };
              })
              .all();
          })
          .all();
      }
      return null;
    },
  },
  methods: {
    getItems(files) {
      if (this.showAllItems) {
        return files;
      }

      return files.filter(
        (file) => file.content_format.length > 0 || file.images_valid.length > 0
      );
    },
    validContent(file) {
      const errors = [];
      const questionResult = /^##\s(?<questionNumber>.*)$/gm.exec(file.data);
      if (questionResult === null) {
        errors.push(
          `${file.filename}: ` +
            this.$t("items.import.Question Number is missing")
        );
      } else {
        Object.assign(file, {
          questionNumber: questionResult[1].trim(),
        });
      }

      if (file.data.indexOf("Correct Answer: ") === -1) {
        errors.push(
          `${file.filename}: ` + this.$t("items.import.Missing Correct Answer")
        );
      }

      return errors;
    },
    validImages(file) {
      const match = /!\[\]\(img\/.*\)/gm.exec(file.data);
      if (match === null) {
        return [];
      }

      return [
        `${file.filename}: ` +
          this.$t("items.import.Missing Images") +
          `: ${match[0]}`,
      ];
    },
    fileAdded(file) {
      if (file.fullPath) {
        if (
          file.fullPath.includes(".md") ||
          file.fullPath.includes(".png") ||
          file.fullPath.includes(".jpg") ||
          file.fullPath.includes(".jpeg")
        ) {
          this.files.push(file);
          this.$refs.dropzone.removeFile(file);
        }
      } else {
        // single file
        Object.assign(file, { fullPath: "Files" });
        this.files.push(file);
      }

      this.transform();
    },
    transform: debounce(async function () {
      const images = await Promise.all(
        collect(
          this.files.filter(
            (file) =>
              file.upload.filename.indexOf(".png") > -1 ||
              file.upload.filename.indexOf(".jpg") > -1 ||
              file.upload.filename.indexOf(".jpeg") > -1
          )
        )
          .map(async (file) => {
            const paths = file.fullPath.split("/");
            const filename = file.upload.filename;
            return {
              data: await this.toBase64(file),
              filename: filename,
              directory: paths[0],
              type: "image",
            };
          })
          .values()
          .all()
      );

      const files = await Promise.all(
        collect(this.files)
          .filter((file) => file.upload.filename.indexOf(".md") > -1)
          .map(async (file) => {
            const paths = file.fullPath.split("/");
            const filename = file.upload.filename;

            let content = (
              await axios.get(URL.createObjectURL(file), {
                baseURL: null,
              })
            ).data;

            // replace images
            images.forEach((image) => {
              if (content.includes(image.filename)) {
                const regex = new RegExp(
                  `!\\[]\\(img\\/${image.filename}\\)`,
                  "g"
                );
                content = content.replace(
                  regex,
                  `<img style="max-width: 50%;" src="${image.data}" />`
                );
              }
            });

            return {
              filename: filename,
              data: content,
              directory: paths[0],
              type: "md",
            };
          })
          .values()
          .all()
      );
      this.mapFiles = collect(files).groupBy("directory").all();
    }, 300),
    async onImporting() {
      this.importLoading = true;
      const jobs = collect(this.mapFiles)
        .map((files) => {
          return files.map((file) => {
            const data = {
              questionNumber: file.questionNumber,
              title: file.data.split("\n", 3)[2],
              content: null,
              a: 0.0,
              b: 0.0,
              c: 0.0,
              maxInfoTheta: 0.0,
            };
            const { content, a, b, c } = this.getParameters(file);
            Object.assign(data, {
              content,
              a,
              b,
              c,
              maxInfoTheta: round(
                b + (1 / 1.7) * a * Math.log(0.5 * (1 + sqrt(1 + 8 * c))),
                3
              ),
            });

            return data;
          });
        })
        .map(async (files, directory) => {
          return await Promise.all(
            files
              .map(async (file) => {
                const { data } = await getItemByQuestionNumber(
                  this.course.id,
                  file.questionNumber
                );

                if (data === "Optional.empty" || data === null) {
                  try {
                    const { id } = await create(this.course.id, file);
                    return {
                      questionNumber: file.questionNumber,
                      mode: "create",
                      id,
                      directory,
                      success: true,
                      message: null,
                    };
                  } catch (e) {
                    return {
                      questionNumber: file.questionNumber,
                      mode: "create",
                      id: null,
                      directory,
                      success: false,
                      message: e.message,
                    };
                  }
                } else {
                  Object.assign(file, {
                    a: data.a,
                    b: data.b,
                    c: data.c,
                    maxInfoTheta: data.maxInfoTheta,
                    course_id: data.course_id,
                    topics: collect(data.topics)
                      .unique("id")
                      .values()
                      .toArray(),
                  });
                  try {
                    await update(data.id, file);
                    return {
                      questionNumber: file.questionNumber,
                      mode: "update",
                      id: data.id,
                      directory,
                      success: true,
                      message: null,
                    };
                  } catch (e) {
                    return {
                      questionNumber: file.questionNumber,
                      mode: "update",
                      id: data.id,
                      directory,
                      success: false,
                      message: e.message,
                    };
                  }
                }
              })
              .all()
          );
        })
        .values()
        .collapse()
        .all();

      collect(await Promise.all(jobs))
        .collapse()
        .each((job) => {
          const files = this.mapFiles[job.directory];
          if (files) {
            const file = files
              .where("questionNumber", job.questionNumber)
              .first();
            if (file) {
              Object.assign(file, {
                mode: job.mode,
                id: job.id,
                success: job.success,
                message: job.message,
              });
            }
          }
        });

      this.importLoading = false;
      this.reload = !this.reload;
    },
    getParameters(file) {
      if (file.data.split("### Item Parameters").length === 2) {
        const content = file.data.split("### Item Parameters")[0];
        return {
          content,
          a: Number(file.data.split("parameter_a: ")[1].split("\n")[0]),
          b: Number(file.data.split("parameter_b: ")[1].split("\n")[0]),
          c: Number(file.data.split("parameter_c: ")[1].split("\n")[0]),
        };
      } else {
        return {
          content: file.data,
          a: round((Math.random() * 1999) / 1000 + 0.001, 3),
          b: round(
            (Math.random() * 3000) / 1000 - (Math.random() * 3000) / 1000,
            3
          ),
          // C參數(猜測率)設定
          c: this.guessCParameter(file.data),
        };
      }
    },
    guessCParameter(response) {
      const answerAmount = collect(
        response.split("Correct Answer: ")[1].split("\n")[0].trim().split(",")
      )
        .map((value) => value.trim())
        .toArray();

      return round(
        1 /
          combinations(this.guessCOptionAmount(response), answerAmount.length),
        3
      );
    },
    guessCOptionAmount(response) {
      if (response.includes("G.")) {
        return 7;
      }
      if (response.includes("F.")) {
        return 6;
      }
      if (response.includes("E.")) {
        return 5;
      }

      if (response.includes("D.")) {
        return 4;
      }
      return 3;
    },
    toBase64(file) {
      var reader = new FileReader();

      return new Promise((resolve, reject) => {
        reader.onerror = () => {
          reader.abort();
          reject(new DOMException("Problem parsing input file."));
        };

        reader.onload = () => {
          resolve(reader.result);
        };
        reader.readAsDataURL(file);
      });
    },
  },
};
</script>
