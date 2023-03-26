<template>
  <v-container fluid>
    <v-card class="mt-3">
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          <v-btn
            color="white"
            @click="showTreeViewCategory = !showTreeViewCategory"
            icon
          >
            <v-icon> mdi-file-tree-outline </v-icon>
          </v-btn>
          <span>
            {{ $t("items.board.Item Board") }}
          </span>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-text-field
          :label="$t('Search')"
          v-model="search"
          class="mx-2 mx-md-4"
          style="max-width: 450px"
          append-icon="mdi-magnify"
          dense
          outlined
          filled
          hide-details
          single-line
        >
        </v-text-field>
        <v-btn
          icon
          small
          color="white"
          :loading="loading"
          @click="refreshItems"
        >
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-app-bar>
      <v-card-text>
        <v-row>
          <v-slide-x-transition hide-on-leave>
            <v-col
              v-show="showTreeViewCategory"
              cols="4"
              style="max-height: calc(100vh - 188px); overflow-y: auto"
            >
              <div class="d-flex">
                <v-btn
                  text
                  :disabled="_topicActive.length === 0"
                  @click="_topicActive = []"
                >
                  {{ $t("items.board.All Items") }}
                </v-btn>
              </div>
              <topic-treeview :course="course" :active.sync="_topicActive">
              </topic-treeview>
            </v-col>
          </v-slide-x-transition>
          <v-col>
            <v-data-table
              show-expand
              item-key="questionNumber"
              :headers="headers"
              :items="mapItems"
              :loading="loading"
              :options.sync="itemPagination"
              :items-per-page="10"
              :server-items-length="totalItems"
              :expanded.sync="expanded"
              expand-icon="mdi-eye"
              :footer-props="{ 'items-per-page-options': [10, 50, 100, -1] }"
              hide-default-header
              @item-expanded="onPreviewItem"
            >
              <template v-slot:header="{ props }">
                <client-sort-data-table
                  :props="props"
                  :sort-by.sync="sortBy"
                  :sort-desc.sync="sortDesc"
                >
                </client-sort-data-table>
              </template>
              <template v-slot:item.actions="{ item }">
                <v-btn
                  icon
                  :to="{
                    name: 'items.edit',
                    params: { questionNumber: item.questionNumber },
                  }"
                >
                  <v-icon> mdi-pencil </v-icon>
                </v-btn>
              </template>

              <template v-slot:expanded-item="{ headers, item }">
                <td :colspan="headers.length">
                  <v-progress-linear v-if="!item.previewLoaded" indeterminate>
                  </v-progress-linear>
                  <preview-card
                    v-else
                    :item="item"
                    :closeable="false"
                  ></preview-card>
                </td>
              </template>
            </v-data-table>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import { getItems } from "@/services/ItemApi";
import bus from "@/components/core/bus";
import { debounce } from "vue-debounce";
import PreviewCard from "@/components/items/PreviewCard";
import collect from "collect.js";
import ClientSortDataTable from "@/components/core/ClientSortDataTable.vue";
import contentLazyLoad from "@/components/items/contentLazyLoad";
import TopicTreeview from "@/components/topics/TopicTreeview.vue";
import { getItemsByTopic } from "@/services/TopicApi";

export default {
  name: "Board",
  components: { PreviewCard, ClientSortDataTable, TopicTreeview },
  mixins: [contentLazyLoad],
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      sortBy: [],
      sortDesc: [],
      expanded: [],
      items: [],
      itemPagination: {},
      totalItems: null,
      loading: false,
      topicActive: [],
      search: "",
      showTreeViewCategory: true,
    };
  },
  storage: {
    keys: ["showTreeViewCategory"],
    namespace: "app",
  },
  watch: {
    search() {
      this.itemPagination.page = 1;
      this.fetch();
    },
    "itemPagination.page": {
      handler() {
        this.fetch();
      },
    },
    "itemPagination.itemsPerPage": {
      handler() {
        this.fetch();
      },
    },
    topicActive: {
      handler() {
        this.itemPagination.page = 1;
        this.fetch();
      },
      deep: true,
    },
  },
  methods: {
    fetch: debounce(async function () {
      try {
        this.loading = true;
        let data, pagination;
        if (this.topicActive.length === 0) {
          const result = await getItems(this.course.id, this.search, {
            itemsPerPage: this.itemPagination?.itemsPerPage ?? 10,
            page: this.itemPagination?.page ?? 1,
          });
          data = result.data;
          pagination = result.pagination;
        } else {
          const result = await getItemsByTopic(
            this.topicActive[0].id,
            [],
            this.search,
            {
              itemsPerPage: this.itemPagination?.itemsPerPage ?? 10,
              page: this.itemPagination?.page ?? 1,
            }
          );
          data = result.data;
          pagination = result.pagination;
        }

        this.items = collect(data)
          .map((item) => ({
            ...item,
            previewLoaded: false,
          }))
          .toArray();
        this.totalItems = pagination.total;
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    }, 400),
    async refreshItems() {
      this.itemPagination.page = 1;
      this.fetch();
    },
  },
  computed: {
    headers() {
      // θ_max: theta that maximize the test info functioon.
      return [
        {
          text: this.$t("items.ID"),
          align: "start",
          sortable: true,
          value: "questionNumber",
        },
        { text: this.$t("items.Title"), value: "title" },
        { text: "a", value: "a" },
        { text: "b", value: "b" },
        { text: "c", value: "c" },
        { text: "θ_max", value: "maxInfoTheta" },
        { text: this.$t("items.Actions"), value: "actions", sortable: false },
      ];
    },
    mapItems() {
      let items = collect(this.items);

      if (this.sortBy?.length > 0) {
        const sortColumn = this.sortBy[0];
        if (this.sortDesc[0]) {
          items = items.sortByDesc(sortColumn);
        } else {
          items = items.sortBy(sortColumn);
        }
      }

      return items.values().all();
    },
    _topicActive: {
      set(value) {
        this.topicActive = value;
        this.expanded = [];
      },
      get() {
        return this.topicActive;
      },
    },
  },
};
</script>

<style scoped></style>
