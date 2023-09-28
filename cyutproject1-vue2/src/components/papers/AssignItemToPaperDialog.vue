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
          <v-btn text @click="onAdd"> {{ $t("Add") }}</v-btn>
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
                <v-chip
                  v-if="filterItem"
                  color="warning"
                  close
                  class="text-wrap"
                  style="height: auto"
                  @click:close="onFilterRelations(null)"
                >
                  {{ filterItem.questionNumber }} {{ filterItem.title }}
                </v-chip>
                <v-text-field
                  v-model="search"
                  :label="$t('Search')"
                  append-icon="mdi-magnify"
                >
                </v-text-field>
                <DataTable
                  v-if="!filterItem"
                  class="p-datatable-sm"
                  :selection.sync="itemSelected"
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
                  collapsedRowIcon="mdi mdi-eye v-icon"
                  expandedRowIcon="mdi mdi-eye v-icon"
                  :currentPageReportTemplate="currentPageReportTemplate"
                  removableSort
                  showGridlines
                  lazy
                  :paginator="!filterItem"
                  @page="onPage($event)"
                  @sort="onSort($event)"
                  @row-expand="previewItemInTheSection"
                >
                  <Column
                    selectionMode="multiple"
                    :headerStyle="{ width: '3em' }"
                  ></Column>
                  <Column
                    field="questionNumber"
                    :header="$t('items.ID')"
                    :sortable="true"
                    :style="{ width: '10%' }"
                  >
                    <template v-slot:body="{ data }">
                      <v-btn
                        text
                        color="primary"
                        @click="onFilterRelations(data)"
                      >
                        <v-icon> mdi-relation-many-to-many</v-icon>
                        {{ data.questionNumber }}
                      </v-btn>
                    </template>
                  </Column>
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
                  <Column :expander="true" :style="{ width: '5%' }" />
                  <template #empty>
                    {{
                      !topicSelected
                        ? $t("papers.Please Select Topic")
                        : $t("No data available")
                    }}
                  </template>
                </DataTable>
                <DataTable
                  v-else
                  class="p-datatable-sm"
                  :selection.sync="itemSelected"
                  :value="mapItems"
                  :totalRecords="totalItems"
                  :expandedRows.sync="expanded"
                  :resizableColumns="true"
                  columnResizeMode="fit"
                  responsiveLayout="scroll"
                  collapsedRowIcon="mdi mdi-eye v-icon"
                  expandedRowIcon="mdi mdi-eye v-icon"
                  :filters="{ global: { value: search } }"
                  :globalFilterFields="['questionNumber', 'title']"
                  removableSort
                  showGridlines
                  @row-expand="previewItemInTheSection"
                >
                  <Column
                    selectionMode="multiple"
                    :headerStyle="{ width: '3em' }"
                  ></Column>
                  <Column
                    field="questionNumber"
                    :header="$t('items.ID')"
                    :sortable="true"
                    :style="{ width: '10%' }"
                  >
                  </Column>
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
                  <Column :expander="true" :style="{ width: '5%' }" />
                  <template #empty>
                    {{
                      !topicSelected
                        ? $t("papers.Please Select Topic")
                        : $t("No data available")
                    }}
                  </template>
                </DataTable>
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
import { debounce } from "vue-debounce";
import { getItemsByTopic, getTopics } from "@/services/TopicApi";
import PreviewCard from "@/components/items/PreviewCard";
import collect from "collect.js";
import bus from "@/components/core/bus";
import contentLazyLoad from "@/components/items/contentLazyLoad";
import dataTableService from "@/components/core/dataTable/dataTableService";
import { getItems, links } from "@/services/ItemApi";

export default {
  name: "AssignItemToPaperDialog",
  mixins: [contentLazyLoad, dataTableService],
  props: {
    value: Array,
    course: {
      type: Object,
      required: true,
    },
    defaultFilterItem: {
      type: Object,
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
      totalItems: null,
      expanded: [],
      filterItem: null,
      keepOldSearch: null,
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
        if (this.defaultFilterItem) {
          this.filterItem = Object.assign({}, this.defaultFilterItem);
        }
        this.itemSelected = [];
        this.dataTablePagination.page = 1;
        await this.fetchItems();
      }
    },
    topicSelected: {
      handler() {
        this.dataTablePagination.page = 1;
        this.fetchItems();
      },
      deep: true,
    },
    dataTablePagination: {
      handler() {
        this.fetchItems();
      },
      deep: true,
    },
    search: {
      handler() {
        this.dataTablePagination.page = 1;
        this.fetchItems();
      },
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
  },
  asyncComputed: {
    topicTreeItems: {
      async get() {
        const items = await getTopics(this.course.id);
        return [
          {
            id: null,
            name: this.$t("All Topics"),
            children: items,
          },
        ];
      },
      default: [],
    },
  },
  methods: {
    async previewItemInTheSection({ data }) {
      await this.previewItemOnPrimeVueTable({ data });
      this.editedItem = data;
    },
    fetchItems: debounce(async function () {
      if (this.filterItem) {
        return this.relationFetch();
      }

      if (!this.topicSelected || this.topicSelected.length === 0) {
        return [];
      }

      const except = collect(this.value).pluck("id").all();

      try {
        this.loading = true;
        let response = null;
        if (this.topicSelected[0].id) {
          response = await getItemsByTopic(
            this.topicSelected[0].id,
            except,
            this.search,
            {
              itemsPerPage: this.dataTablePagination?.itemsPerPage ?? 10,
              page: this.dataTablePagination?.page ?? 1,
            }
          );
        } else {
          response = await getItems(this.course.id, this.search, {
            itemsPerPage: this.dataTablePagination?.itemsPerPage ?? 10,
            page: this.dataTablePagination?.page ?? 1,
          });
        }

        this.totalItems = response?.pagination.total;
        this.items = response?.data;
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    }, 400),
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
      this.filterItem = null;
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
    onFilterRelations(data) {
      if (data === null) {
        this.search = this.keepOldSearch;
        this.keepOldSearch = null;
      } else {
        this.keepOldSearch = this.search;
        this.search = null;
      }
      this.filterItem = data;
      this.fetchItems();
    },
    async relationFetch() {
      try {
        this.loading = true;
        const { data } = await links(this.filterItem.id);

        const itemsRecently = collect(this.value);
        this.items = collect(data)
          .reject((item) => {
            return itemsRecently.where("id", item.linkedItemId).isNotEmpty();
          })
          .map((item) => ({
            id: item.linkedItemId,
            title: item.title,
            questionNumber: item.questionNumber,
            course_id: this.course.id,
            topics: item.topics,
          }))
          .toArray();
        this.totalItems = data.length;
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    },
    open() {
      this.dialog = true;
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
