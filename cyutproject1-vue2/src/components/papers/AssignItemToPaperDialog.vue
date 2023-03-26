<template>
  <v-dialog v-model="dialog" fullscreen persistent>
    <template v-slot:activator="{ on, attrs }">
      <v-btn color="primary" v-bind="attrs" v-on="on">
        {{ $t("Add") }} {{ $t("papers.Question") }}
      </v-btn>
    </template>

    <v-card>
      <v-toolbar dark color="primary">
        <v-btn icon dark @click="onClose">
          <v-icon>mdi-close</v-icon>
        </v-btn>
        <v-toolbar-title>{{ $t("papers.Add Question") }}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-toolbar-items>
          <v-btn text @click="onAdd"> {{ $t("Add") }} </v-btn>
        </v-toolbar-items>
      </v-toolbar>

      <v-card-text>
        <v-row>
          <v-col
            cols="12"
            md="6"
            lg="4"
            :class="{ 'content-height': $vuetify.breakpoint.mdAndUp }"
          >
            <v-card-text class="treeview-container">
              <v-treeview
                :items="topicTreeItems"
                @update:active="(item) => (topicSelected = item)"
                open-all
                activatable
                return-object
              >
                <template v-slot:label="{ item }">
                  {{ item.user_topic_id }} {{ item.name }}
                </template>
              </v-treeview>
            </v-card-text>
          </v-col>
          <v-divider vertical></v-divider>
          <v-col
            cols="12"
            md="6"
            lg="8"
            :class="{ 'content-height': $vuetify.breakpoint.mdAndUp }"
          >
            <v-card-text style="height: calc(100vh - 200px)">
              <div
                class="px-1"
                ref="tableContainer"
                :style="{ height: `${tableHeight}px`, 'overflow-y': 'auto' }"
              >
                <v-data-table
                  v-model="itemSelected"
                  :headers="headers"
                  :items="items"
                  item-key="questionNumber"
                  :search="search"
                  :loading="loading"
                  :items-per-page="10"
                  :footer-props="{
                    'items-per-page-options': [10, 50, 100, -1],
                  }"
                  :options.sync="itemPagination"
                  :server-items-length="totalItems"
                  show-select
                  :no-data-text="
                    !topicSelected
                      ? $t('papers.Please Select Topic')
                      : '$vuetify.noDataText'
                  "
                >
                  <template v-slot:top>
                    <v-text-field
                      v-model="search"
                      :label="$t('Search')"
                      append-icon="mdi-magnify"
                    >
                    </v-text-field>
                  </template>
                  <template v-slot:item.actions="{ item }">
                    <v-icon @click="onPreviewItem(item)">mdi-eye</v-icon>
                  </template>
                </v-data-table>
              </div>
              <v-divider
                ref="previewDivider"
                class="py-4"
                style="cursor: ns-resize"
              >
              </v-divider>
              <div
                :style="{
                  'overflow-y': 'auto',
                  height: `calc(100vh - ${tableHeight + 180}px)`,
                }"
              >
                <preview-card
                  :item="editedItem"
                  @close="editedItem = null"
                  closeable
                >
                </preview-card>
              </div>
            </v-card-text>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import { getItemsByTopic, getTopics } from "@/services/TopicApi";
import PreviewCard from "@/components/items/PreviewCard";
import collect from "collect.js";
import bus from "@/components/core/bus";
import { getItemByQuestionNumber } from "@/services/ItemApi";

export default {
  name: "AssignItemToPaperDialog",
  props: {
    value: Array,
    course: {
      type: Object,
      required: true,
    },
  },
  components: {
    PreviewCard,
  },
  data() {
    return {
      initial: false,
      dialog: false,
      tableHeight: window.innerHeight / 2,
      search: null,
      loading: false,
      itemSelected: [],
      items: [],
      editedItem: null,
      topicSelected: false,
      itemPagination: {},
      totalItems: null,
    };
  },
  watch: {
    async dialog(value) {
      if (!value) {
        this.items = [];
      }
      if (value && !this.initial) {
        setTimeout(() => {
          this.setEvent();
        }, 250);
      }

      if (value) {
        this.itemSelected = [];
        this.itemPagination.page = 1;
        await this.fetchItems();
      }
    },
    topicSelected: {
      handler() {
        if (this.itemPagination.page === 1) {
          this.fetchItems();
          return;
        }
        this.itemPagination.page = 1;
      },
      deep: true,
    },
    itemPagination: {
      handler() {
        this.fetchItems();
      },
      deep: true,
    },
  },
  computed: {
    headers() {
      return [
        {
          text: this.$t("papers.ID"),
          align: "start",
          sortable: true,
          value: "questionNumber",
        },
        { text: this.$t("papers.Title"), value: "title" },
        { text: this.$t("papers.Actions"), value: "actions" },
      ];
    },
  },
  asyncComputed: {
    topicTreeItems: {
      async get() {
        return await getTopics(this.course.id);
      },
      default: [],
    },
  },
  methods: {
    async onPreviewItem(item) {
      if (!item.content) {
        const { data } = await getItemByQuestionNumber(
          this.course.id,
          item.questionNumber
        );
        Object.assign(item, data, {
          previewLoaded: true,
        });
      }
      this.editedItem = item;
    },
    async fetchItems() {
      if (!this.topicSelected || this.topicSelected.length === 0) {
        return [];
      }

      const except = collect(this.value).pluck("id").all();

      try {
        this.loading = true;
        const { data, pagination } = await getItemsByTopic(
          this.topicSelected[0].id,
          except,
          this.search,
          {
            itemsPerPage: this.itemPagination?.itemsPerPage ?? 10,
            page: this.itemPagination?.page ?? 1,
          }
        );
        this.totalItems = pagination.total;
        this.items = data;
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    },
    onAdd() {
      this.$emit(
        "input",
        collect([...this.itemSelected, ...this.value])
          .unique("id")
          .values()
          .all()
      );
      this.onClose();
    },
    onClose() {
      this.dialog = false;
    },
    setEvent() {
      const el = this.$refs.previewDivider.$el;
      const vm = this;

      function resize(e) {
        document.body.style.cursor = "ns-resize";
        const offsetY = e.pageY - 150;
        if (offsetY < 300 || offsetY > window.innerHeight - 300) {
          return;
        }
        vm.tableHeight = offsetY;
      }

      el.addEventListener("mousedown", () => {
        el.style.transition = "initial";
        document.addEventListener("mousemove", resize, false);
      });

      document.addEventListener(
        "mouseup",
        () => {
          el.style.transition = "";
          document.body.style.cursor = "";
          document.removeEventListener("mousemove", resize, false);
        },
        false
      );
      vm.initial = true;
    },
  },
};
</script>

<style scoped>
.treeview-container {
  overflow-y: auto;
  height: calc(100vh - 84px);
}
</style>
