<template>
  <v-container fluid>
    <ValidationObserver v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(onSubmit)">
        <v-card>
          <v-app-bar dark color="#555555">
            <v-toolbar-title>
              {{ $t("Edit") }} {{ $t("papers.Paper") }}
            </v-toolbar-title>
            <v-chip class="ma-2" color="primary" text-color="white">
              {{ form.items.length }} {{ $t("papers.unitOfQuestions") }}
            </v-chip>
            <v-dialog v-model="deleteItemConfirmation" max-width="500">
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  :disabled="selected.length === 0"
                  color="warning"
                  class="mr-3"
                  v-bind="attrs"
                  v-on="on"
                >
                  {{ $t("Delete") }} {{ $t("papers.Questions") }}
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5">
                  {{ $t("papers.Are you sure to delete the question?") }}
                </v-card-title>
                <v-card-text>
                  {{
                    $t(
                      "papers.Make sure to delete the question from the paper?"
                    )
                  }}
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>

                  <v-btn text @click="deleteItemConfirmation = false">
                    {{ $t("Cancel") }}
                  </v-btn>

                  <v-btn color="primary" @click="onDeletedItemConfirmation">
                    {{ $t("Yes, Delete") }}
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>

            <assign-item-to-paper-dialog
              v-model="form.items"
              :course="course"
            ></assign-item-to-paper-dialog>
            <calculate-max-misclassification-probability
              :items="form.items"
              :course="course"
            ></calculate-max-misclassification-probability>
            <v-spacer></v-spacer>
            <v-dialog v-model="deletePaperConfirmation" max-width="500">
              <template v-slot:activator="{ on, attrs }">
                <v-btn color="error" v-bind="attrs" v-on="on" class="mr-3">
                  {{ $t("Delete") }} {{ $t("papers.Paper") }}
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5">
                  {{ $t("papers.Are you sure to delete the paper?") }}
                </v-card-title>
                <v-card-text>
                  {{ $t("papers.This will be permanent") }}</v-card-text
                >
                <v-card-actions>
                  <v-spacer></v-spacer>

                  <v-btn
                    v-if="!deletePaperLoading"
                    text
                    @click="deletePaperConfirmation = false"
                  >
                    {{ $t("Cancel") }}
                  </v-btn>

                  <v-btn
                    color="primary"
                    @click="onDeletedPaperConfirmation"
                    :loading="deletePaperLoading"
                  >
                    {{ $t("Yes, Delete") }}
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <v-btn v-if="!loading" :to="{ name: 'papers.list' }" color="teal">
              {{ $t("Cancel") }}
            </v-btn>
            <v-btn
              :loading="loading"
              color="primary"
              type="submit"
              class="ml-3"
            >
              {{ $t("Submit") }}
            </v-btn>
            <v-menu offset-x left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-bind="attrs" v-on="on">
                  <v-icon>mdi-export</v-icon>
                </v-btn>
              </template>
              <v-card>
                <v-card-title>
                  {{ $t("papers.Exports") }}
                </v-card-title>
                <v-card-actions>
                  <v-menu offset-y>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        color="primary"
                        class="mx-2"
                        v-bind="attrs"
                        v-on="on"
                        :disabled="form && form.items.length === 0"
                        :loading="exporting"
                      >
                        {{ $t("papers.Export Quiz Paper") }}
                      </v-btn>
                    </template>
                    <v-list>
                      <v-list-item @click="exportQuestionPaper('html')">
                        <v-list-item-title>
                          {{ $t("Export to HTML") }}
                        </v-list-item-title>
                      </v-list-item>
                      <v-list-item @click="exportQuestionPaper('md')">
                        <v-list-item-title>
                          {{ $t("Export to Markdown") }}
                        </v-list-item-title>
                      </v-list-item>
                    </v-list>
                  </v-menu>
                  <v-menu offset-y>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        color="primary"
                        class="mx-2"
                        v-bind="attrs"
                        v-on="on"
                        :disabled="form && form.items.length === 0"
                        :loading="exporting"
                      >
                        {{ $t("papers.Export Answer Paper") }}
                      </v-btn>
                    </template>
                    <v-list>
                      <v-list-item @click="exportQuestionAndAnswer('html')">
                        <v-list-item-title>
                          {{ $t("Export to HTML") }}
                        </v-list-item-title>
                      </v-list-item>
                      <v-list-item @click="exportQuestionAndAnswer('md')">
                        <v-list-item-title>
                          {{ $t("Export to Markdown") }}
                        </v-list-item-title>
                      </v-list-item>
                    </v-list>
                  </v-menu>
                </v-card-actions>
              </v-card>
            </v-menu>
          </v-app-bar>
          <v-sheet style="overflow-y: auto; max-height: calc(100vh - 136px)">
            <v-card-text>
              <fields :form="form"></fields>
            </v-card-text>
            <v-subheader>{{ $t("papers.Topics") }}</v-subheader>
            <v-card-text>
              <v-chip class="ma-1" v-for="topic in itemsTopics" :key="topic">
                {{ topic }}
              </v-chip>
            </v-card-text>
            <v-divider></v-divider>
            <v-card-text>
              <v-data-table
                v-model="selected"
                :items="form.items"
                :headers="headers"
                :expanded.sync="expanded"
                item-key="id"
                expand-icon="mdi-eye"
                @click:row="onRowClick"
                @item-expanded="onPreviewItem"
                show-select
                show-expand
                disable-pagination
                hide-default-footer
              >
                <template v-slot:top>
                  <v-toolbar flat>
                    <v-toolbar-title>
                      {{ $t("papers.Questions") }}
                    </v-toolbar-title>
                    <v-divider class="mx-4" inset vertical></v-divider>
                    <v-spacer></v-spacer>
                  </v-toolbar>
                </template>
                <template v-slot:item.topics="{ item }">
                  <v-chip
                    v-for="topic in item.topics"
                    :key="`${item.id}_${topic.id}`"
                    class="ma-2"
                  >
                    {{ topic.user_topic_id }} {{ topic.name }}
                  </v-chip>
                </template>

                <template v-slot:expanded-item="{ headers, item }">
                  <td :colspan="headers.length">
                    <preview-card
                      :item="item"
                      :closeable="false"
                    ></preview-card>
                  </td>
                </template>
              </v-data-table>
            </v-card-text>
          </v-sheet>
        </v-card>
      </v-form>
    </ValidationObserver>
  </v-container>
</template>

<script>
import showdown from "showdown";
import fileDownloader from "js-file-download";
import form from "@/components/papers/form";
import Fields from "@/components/papers/Fields";
import { deletePaper, getPaper, updatePaper } from "@/services/paperApi";
import { parseISO, format } from "date-fns";
import collect from "collect.js";
import bus from "@/components/core/bus";
import PreviewCard from "@/components/items/PreviewCard";
import AssignItemToPaperDialog from "@/components/papers/AssignItemToPaperDialog";
import CalculateMaxMisclassificationProbability from "@/components/papers/CalculateMaxMisclassificationProbability.vue";
import contentLazyLoad from "@/components/items/contentLazyLoad";

const converter = new showdown.Converter();

export default {
  name: "Edit",
  mixins: [form, contentLazyLoad],
  props: {
    paperId: {
      type: [Number, String],
    },
    course: {
      type: Object,
      required: true,
    },
  },
  components: {
    AssignItemToPaperDialog,
    CalculateMaxMisclassificationProbability,
    PreviewCard,
    Fields,
  },
  data() {
    return {
      expanded: [],
      tableHeight: window.innerHeight / 2,
      loading: false,
      selected: [],
      deleteItemConfirmation: false,
      deletePaperConfirmation: false,
      deletePaperLoading: false,
      exporting: false,
    };
  },
  computed: {
    headers() {
      return [
        { value: "title", text: this.$t("papers.Title"), sort: false },
        { value: "topics", text: this.$t("papers.Topics"), sort: false },
      ];
    },
    itemsTopics() {
      return collect(this.form.items)
        .pluck("topics")
        .map((items) =>
          collect(items)
            .map((value) => `${value.user_topic_id} ${value.name}`)
            .all()
        )
        .collapse()
        .unique()
        .sortBy((item) => item)
        .values()
        .all();
    },
  },
  methods: {
    async onSubmit() {
      try {
        this.loading = true;
        await updatePaper(this.paperId, {
          course_id: this.course.id,
          title: this.form.title,
          summary: this.form.summary,
          date: this.form.date,
          items: collect(this.form.items).pluck("id").all(),
        });
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("papers.Updated paper successfully"),
        });
        await this.$router.push({ name: "papers.list" });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    },
    onDeletedItemConfirmation() {
      this.deleteItemConfirmation = false;
      this.form.items = collect(this.form.items)
        .reject((item) => {
          return this.selected.indexOf(item) > -1;
        })
        .values()
        .all();
      this.selected = [];
    },
    async onDeletedPaperConfirmation() {
      try {
        this.deletePaperLoading = true;
        await deletePaper(this.paperId);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("papers.Deleted paper successfully"),
        });
        await this.$router.push({ name: "papers.list" });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.response.data.message || e.message,
        });
      } finally {
        this.deletePaperLoading = false;
      }
    },
    preFetchItemContent() {
      return new Promise((resolve) => {
        const jobs = this.form.items.map(
          async (item) =>
            await this.onPreviewItem({
              item,
              value: true,
            })
        );

        Promise.all(jobs).then(() => resolve());
      });
    },
    async exportQuestionPaper(type) {
      try {
        this.exporting = true;
        await this.preFetchItemContent();
        const data = collect(this.form.items)
          .pluck("content")
          .map((content) => {
            return content.split("### Ans:")[0];
          });

        switch (type) {
          case "html":
            fileDownloader(
              converter.makeHtml(data.join("\r\n")),
              `${this.form.title}.html`
            );
            return;
          case "md":
            fileDownloader(data.join("\r\n"), `${this.form.title}.md`);
            return;
        }

        bus.$emit("alert-message", {
          type: "error",
          message: this.$t("papers.Unknown type") + `: ${type}`,
        });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.exporting = false;
      }
    },
    async exportQuestionAndAnswer(type) {
      try {
        this.exporting = true;
        await this.preFetchItemContent();
        const data = collect(this.form.items).pluck("content").join("\r\n");

        switch (type) {
          case "html":
            fileDownloader(converter.makeHtml(data), `${this.form.title}.html`);
            return;
          case "md":
            fileDownloader(data, `${this.form.title}.md`);
            return;
        }
        bus.$emit("alert-message", {
          type: "error",
          message: this.$t("papers.Unknown type") + `: ${type}`,
        });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.exporting = false;
      }
    },
    onRowClick(item) {
      const index = this.expanded.indexOf(item);
      if (index > -1) {
        this.expanded.splice(index, 1);
        return;
      }

      this.expanded.push(item);
      this.onPreviewItem({
        item,
        value: true,
      });
    },
  },
  async mounted() {
    const { data } = await getPaper(this.paperId);
    this.form = {
      ...data,
      date: format(parseISO(data.date), "yyyy-MM-dd"),
    };
    this.selected = [];
  },
};
</script>

<style scoped></style>
