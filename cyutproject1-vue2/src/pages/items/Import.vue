<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("items.import.Import Items") }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn color="primary" elevation="2" @click="getItems">
          {{ $t("items.import.Fetch Items") }}
        </v-btn>
      </v-app-bar>
      <v-card-text v-if="message !== ''">
        <h3 style="color: red">
          {{ message }}
        </h3>
      </v-card-text>
      <v-card-text>
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

      <v-card-text>
        <v-file-input
          accept=".md"
          webkitdirectory
          :label="$t('items.import.Upload')"
          show-size
          small-chips
          v-model="files"
          multiple
          color="#7199de"
          @change="addItem"
          v-if="!isLoading"
        >
          <template v-slot:selection="{ text }">
            <v-chip small label color="#EEEEEE">
              {{ text }}
            </v-chip>
          </template>
        </v-file-input>
      </v-card-text>
      <v-card-text>
        <v-progress-circular
          indeterminate
          color="primary"
          :size="70"
          :width="7"
          v-if="isLoading"
        ></v-progress-circular>
      </v-card-text>
      <v-card-text>
        <div v-for="(md, index) in md" :key="index">
          <vue-markdown :source="md.content"></vue-markdown>
          <hr />
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import axios from "@/services/axios";
import {
  create,
  getItemByQuestionNumber,
  getItems,
  update,
} from "@/services/ItemApi";
import VueMarkdown from "@adapttive/vue-markdown";
import { combinations, sqrt, round } from "mathjs";
import collect from "collect.js";

export default {
  name: "Import",
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      files: null,
      itemUrl: [],
      imageUrl: [],
      md: [],
      count: 0,
      message: "",
      isLoading: false,
    };
  },
  components: {
    VueMarkdown,
  },
  methods: {
    async addItem() {
      this.itemUrl = [];
      this.imageUrl = [];
      this.count = 0;
      this.message = "";
      this.isLoading = true;
      const files = collect(this.files)
        .filter(
          (file) =>
            file.name.includes(".md") ||
            file.name.includes(".png") ||
            file.name.includes(".jpg")
        )
        .values()
        .toArray();
      this.files = files;
      // 將 md 和 圖片轉成 url 和 base64 ，並分別加入各自陣列
      for (let i = 0; i < files.length; i++) {
        if (files[i].name.indexOf(".md") > -1) {
          this.itemUrl.push(URL.createObjectURL(files[i]));
        }
        if (
          files[i].name.indexOf(".png") > -1 ||
          files[i].name.indexOf(".jpg") > -1
        ) {
          let base64Img = await this.toBase64(files[i]);
          this.imageUrl.push({
            imgName: files[i].name,
            url: base64Img,
          });
        }
      }

      try {
        for (let i = 0; i < this.itemUrl.length; i++) {
          const tmpItem = {
            questionNumber: 0,
            title: null,
            content: null,
            count: null,
            a: 0.0,
            b: 0.0,
            c: 0.0,
            maxInfoTheta: 0.0,
            question: "deprecated",
          };

          const regex = /##\s(?<questionNumber>\w+)/gm;

          // files轉string，get資料後存入資料庫
          let response = await axios
            .get(this.itemUrl[i], { baseURL: null })
            .then((response) =>
              response.data.replaceAll("(img/", "(items/img/")
            );

          let match;
          if ((match = regex.exec(response)) === null) {
            continue;
          }

          if (response.split("### Item Parameters").length == 2) {
            this.item.content = response.split("### Item Parameters")[0];
            //匯入參數
            Object.assign(tmpItem, {
              a: Number(response.split("parameter_a: ")[1].split("\n")[0]),
              b: Number(response.split("parameter_b: ")[1].split("\n")[0]),
              c: Number(response.split("parameter_c: ")[1].split("\n")[0]),
            });
          } else {
            Object.assign(tmpItem, {
              content: response,
              a: round((Math.random() * 1999) / 1000 + 0.001, 3),
              b: round(
                (Math.random() * 3000) / 1000 - (Math.random() * 3000) / 1000,
                3
              ),
              // C參數(猜測率)設定
              c: this.guessCParameter(response),
            });
          }

          Object.assign(tmpItem, {
            questionNumber: match.groups.questionNumber,
            title: `${match.groups.questionNumber} ${
              response.split("\n", 3)[2]
            }`,
            count: response.match(/!\[\]\((?!http)(.*)\)/g),
            // 計算最大資訊量能力值
            maxInfoTheta: round(
              tmpItem.b +
                (1 / 1.7) *
                  tmpItem.a *
                  Math.log(0.5 * (1 + sqrt(1 + 8 * tmpItem.c))),
              3
            ),
          });

          // 將圖片 url 覆蓋成 base64
          if (tmpItem.count != null) {
            for (let j = 0; j < tmpItem.count.length; j++) {
              for (let k = 0; k < this.imageUrl.length; k++) {
                if (tmpItem.count[j].includes(this.imageUrl[k].imgName)) {
                  tmpItem.content = tmpItem.content.replace(
                    /!\[\]\(items\/img\/.*\)/,
                    "![](" + this.imageUrl[k].url + ")"
                  );
                  break;
                }
              }
            }
          }
          if (tmpItem.count != null) {
            // 計算圖片總數
            this.count += tmpItem.count.length;
          }
          this.itemUrl[i] = tmpItem;
        }
      } catch (e) {
        this.message = e.message;
        this.isLoading = false;
        return;
      }

      // 判斷圖片數量是否正確
      if (this.count === this.imageUrl.length) {
        for (let i = 0; i < this.itemUrl.length; i++) {
          const { data } = await getItemByQuestionNumber(
            this.course.id,
            this.itemUrl[i].questionNumber
          );

          if (data === "Optional.empty" || data === null) {
            await create(this.course.id, this.itemUrl[i]);
          } else {
            Object.assign(this.itemUrl[i], {
              a: data.a,
              b: data.b,
              c: data.c,
              maxInfoTheta: data.maxInfoTheta,
              course_id: data.course_id,
              topics: collect(data.topics).unique("id").values().toArray(),
            });

            await update(data.id, this.itemUrl[i]);
          }
        }
        this.message = this.$t("items.import.Uploaded successfully");
      } else {
        this.message = this.$t(
          "items.import.Count of images uploaded incorrect"
        );
      }
      this.isLoading = false;
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
    async getItems() {
      const { data } = await getItems(this.course.id, null, {
        page: 1,
        itemsPerPage: 9999,
      });

      this.md = data;
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
  },
};
</script>
