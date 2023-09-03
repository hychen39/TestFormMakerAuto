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
        <v-row no-gutters>
          <v-slide-x-transition hide-on-leave>
            <v-col
              v-show="showTreeViewCategory"
              cols="12"
              sm="4"
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
          <v-col cols="12" sm="8">
            <DataTable
              ref="dataTable"
              class="p-datatable-sm"
              :value="mapItems"
              :rows.sync="dataTablePagination.itemsPerPage"
              :totalRecords="totalItems"
              :loading="loading"
              :rowsPerPageOptions="[10, 50, 100]"
              :expandedRows.sync="expanded"
              :resizableColumns="true"
              columnResizeMode="fit"
              responsiveLayout="scroll"
              paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
              :currentPageReportTemplate="currentPageReportTemplate"
              showGridlines
              lazy
              paginator
              removableSort
              @page="onPage($event)"
              @sort="onSort($event)"
              @row-expand="previewItemOnPrimeVueTable"
            >
              <Column :expander="true" :style="{ width: '5%' }" />
              <Column
                field="questionNumber"
                :header="$t('items.ID')"
                :sortable="true"
                :style="{ width: '10%' }"
              ></Column>
              <Column
                field="title"
                :header="$t('items.Title')"
                :sortable="true"
                :style="{ width: '60%' }"
              >
                <template v-slot:body="{ data }">
                  <div class="text-break text-wrap">
                    {{ data.title }}
                  </div>
                </template>
              </Column>
              <Column
                field="a"
                header="a"
                :sortable="true"
                :style="{ width: '5%' }"
              ></Column>
              <Column
                field="b"
                header="b"
                :sortable="true"
                :style="{ width: '5%' }"
              ></Column>
              <Column
                field="c"
                header="c"
                :sortable="true"
                :style="{ width: '5%' }"
              ></Column>
              <Column
                field="maxInfoTheta"
                header="θ_max"
                :sortable="true"
                :style="{ width: '5%' }"
              ></Column>
              <Column :header="$t('items.Actions')" :style="{ width: '5%' }">
                <template v-slot:body="{ data }">
                  <v-btn
                    icon
                    :to="{
                      name: 'items.edit',
                      params: {
                        questionNumber: data.questionNumber,
                      },
                      query: dataTableQueries,
                    }"
                  >
                    <v-icon> mdi-pencil</v-icon>
                  </v-btn>
                </template>
              </Column>
              <template v-slot:expansion="{ data }">
                <div>
                  <v-progress-linear v-if="!data.previewLoaded" indeterminate>
                  </v-progress-linear>
                  <preview-card
                    v-else
                    :item="data"
                    :closeable="false"
                  ></preview-card>
                </div>
              </template>
              <template #empty>
                {{ $t("No data available") }}
              </template>
            </DataTable>
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
import contentLazyLoad from "@/components/items/contentLazyLoad";
import TopicTreeview from "@/components/topics/TopicTreeview.vue";
import { getItemsByTopic } from "@/services/TopicApi";
import dataTableService from "@/components/core/dataTable/dataTableService";

export default {
  name: "Board",
  components: { PreviewCard, TopicTreeview },
  mixins: [contentLazyLoad, dataTableService],
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      expanded: [],
      items: [],
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
      this.dataTablePagination.page = 1;
      this.fetch();
    },
    dataTablePagination: {
      handler() {
        this.fetch();
      },
      deep: true,
    },
    topicActive: {
      handler() {
        this.dataTablePagination.page = 1;
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
            itemsPerPage: this.dataTablePagination?.itemsPerPage ?? 10,
            page: this.dataTablePagination?.page ?? 1,
          });
          data = result.data;
          pagination = result.pagination;
        } else {
          const result = await getItemsByTopic(
            this.topicActive[0].id,
            [],
            this.search,
            {
              itemsPerPage: this.dataTablePagination?.itemsPerPage ?? 10,
              page: this.dataTablePagination?.page ?? 1,
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
        this.setPaginationToUrl({
          search: this.search,
          topicActive:
            this.topicActive.length > 0
              ? this.topicActive[0].id.toString()
              : null,
        });
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
      this.dataTablePagination.page = 1;
      this.fetch();
    },
  },
  computed: {
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
  mounted() {
    const { search, topicActive } = this.$route.query;
    if (search) {
      this.search = search;
    }
    if (topicActive) {
      this.topicActive = [{ id: parseInt(topicActive, 10) }];
    }
    this.initialPages();
    this.fetch();
  },
};
</script>

<style scoped></style>
