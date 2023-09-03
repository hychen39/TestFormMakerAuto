<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("items.assignTopics.topMenu") }}
        </v-toolbar-title>

        <v-spacer></v-spacer>

        <v-btn color="white" @click="refreshItems" fab icon small>
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-app-bar>
      <v-row>
        <v-col
          :class="{ 'content-height': $vuetify.breakpoint.mdAndUp }"
          :style="{
            width: `${topicTreeWidth}px`,
            'max-width': `${topicTreeWidth}px`,
          }"
        >
          <v-card-text>
            <v-treeview :items="topicTreeItems" open-all>
              <template v-slot:label="{ item }">
                <div
                  v-if="item.parent_id === null"
                  class="white-space-normal my-2"
                >
                  {{ item.user_topic_id }} {{ item.name }}
                </div>
                <div
                  v-else
                  class="white-space-normal my-2"
                  @drop="(e) => onDrop(e, item)"
                  @dragover="onDragover"
                  @dragleave="onDragLeave"
                >
                  {{ item.user_topic_id }} {{ item.name }}
                </div>
              </template>
            </v-treeview>
          </v-card-text>
        </v-col>
        <v-hover v-slot="{ hover }">
          <div
            ref="topicDivider"
            class="d-flex"
            style="cursor: ew-resize; flex-direction: column"
          >
            <v-divider
              vertical
              :style="{
                borderWidth: '0 3px 0 0',
                borderColor: hover ? '#1565C0' : null,
                height: '50%',
                maxHeight: '50%',
                minHeight: '50%',
                alignSelf: 'center',
              }"
            ></v-divider>
            <v-icon :color="hover ? 'blue darken-3' : null" class="my-8"
              >mdi-arrow-left-right</v-icon
            >
            <v-divider
              vertical
              :style="{
                borderWidth: '0 3px 0 0',
                borderColor: hover ? '#1565C0' : null,
                height: '50%',
                maxHeight: '50%',
                minHeight: '50%',
                alignSelf: 'center',
              }"
            ></v-divider>
          </div>
        </v-hover>
        <v-col :class="{ 'content-height': $vuetify.breakpoint.mdAndUp }">
          <v-card-text style="height: calc(100vh - 200px)">
            <div
              class="px-1"
              ref="tableContainer"
              :style="{ height: `${tableHeight}px`, 'overflow-y': 'auto' }"
            >
              <v-data-table
                item-key="questionNumber"
                :headers="headers"
                :items="mapItems"
                :options.sync="itemPagination"
                :loading="loading"
                :server-items-length="totalItems"
                :items-per-page="10"
                :footer-props="{ 'items-per-page-options': [10, 50, 100, -1] }"
                hide-default-header
              >
                <template v-slot:header="{ props }">
                  <client-sort-data-table
                    :props="props"
                    :sort-by.sync="sortBy"
                    :sort-desc.sync="sortDesc"
                  >
                  </client-sort-data-table>
                </template>
                <template v-slot:top>
                  <v-toolbar flat class="mt-3">
                    <v-text-field
                      v-model="search"
                      :label="$t('Search')"
                      append-icon="mdi-magnify"
                    >
                    </v-text-field>
                    <v-spacer></v-spacer>
                    <v-switch
                      :label="$t('items.assignTopics.Non-classified items')"
                      v-model="showNonClassified"
                    >
                    </v-switch>
                  </v-toolbar>
                </template>
                <template v-slot:body="{ items }">
                  <tbody>
                    <tr
                      v-for="item in items"
                      :key="item.title"
                      draggable="true"
                      :itemid="item.id"
                      @dragstart="(e) => onDragStart(e, item)"
                      @dragend="(e) => onDragend(e, item)"
                      @click="displayItem(item)"
                    >
                      <td>{{ item.questionNumber }}</td>
                      <td>{{ item.title }}</td>
                      <td>
                        <v-chip
                          v-for="topic in item.topics"
                          :key="topic.id"
                          class="ma-2"
                        >
                          {{ topic.user_topic_id }} {{ topic.name }}
                        </v-chip>
                      </td>
                      <td>
                        <v-icon @click="displayItem(item)">mdi-eye</v-icon>
                      </td>
                    </tr>
                  </tbody>
                </template>
              </v-data-table>
            </div>
            <v-hover v-slot="{ hover }">
              <div
                ref="previewDivider"
                class="d-flex align-center"
                style="cursor: ns-resize"
              >
                <v-divider
                  :style="{
                    borderWidth: '3px 0 0 0',
                    borderColor: hover ? '#1565C0' : null,
                  }"
                ></v-divider>
                <v-icon :color="hover ? 'blue darken-3' : null" class="mx-8">
                  mdi-arrow-up-down
                </v-icon>
                <v-divider
                  :style="{
                    borderWidth: '3px 0 0 0',
                    borderColor: hover ? '#1565C0' : null,
                  }"
                ></v-divider>
              </div>
            </v-hover>

            <div
              :style="{
                'overflow-y': 'auto',
                height: `calc(100vh - ${tableHeight + 180}px)`,
              }"
            >
              <preview-card
                :item="editedItem"
                closeable
                topic-detachable
                @close="editedItem = null"
                @detachedTopic="onDetachedTopic"
              >
              </preview-card>
            </div>
          </v-card-text>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import collect from "collect.js";
import { getItemByQuestionNumber, getItems, update } from "@/services/ItemApi";
import { getTopics } from "@/services/TopicApi";
import bus from "@/components/core/bus";
import PreviewCard from "@/components/items/PreviewCard";
import { debounce } from "vue-debounce";
import ClientSortDataTable from "@/components/core/ClientSortDataTable.vue";

export default {
  name: "AssignTopic",
  components: {
    PreviewCard,
    ClientSortDataTable,
  },
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      items: [],
      selectedTopics: [],
      loading: false,
      itemPagination: {},
      totalItems: null,
      search: "",
      showNonClassified: false,
      editedIndex: -1,
      editedItem: null,
      tableHeight: window.innerHeight / 2,
      topicTreeWidth: window.innerWidth * 0.2,
      sortBy: [],
      sortDesc: [],
    };
  },
  watch: {
    search() {
      this.itemPagination.page = 1;
      this.fetchItems();
    },
    showNonClassified() {
      this.itemPagination.page = 1;
      this.fetchItems();
    },
    "itemPagination.page": {
      handler() {
        this.fetchItems();
      },
    },
    "itemPagination.itemsPerPage": {
      handler() {
        this.fetchItems();
      },
    },
  },
  computed: {
    headers() {
      return [
        {
          text: this.$t("items.assignTopics.ID"),
          align: "start",
          sortable: true,
          value: "questionNumber",
          width: "8%",
        },
        { text: this.$t("items.assignTopics.Title"), value: "title" },
        {
          text: this.$t("items.assignTopics.Topics"),
          value: "topics",
          sortable: false,
        },
        {
          text: this.$t("items.assignTopics.Actions"),
          value: "actions",
          sortable: false,
        },
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
  },
  methods: {
    onDragStart(e, item) {
      e.target.style.opacity = 0.5;
      e.dataTransfer.setData("text/plain", JSON.stringify(item));
    },
    onDragend(e) {
      e.preventDefault();
      e.stopPropagation();
      e.target.parentElement.parentElement.parentElement.classList.remove(
        "topic-tree-hover"
      );
      e.target.style.opacity = 1;
    },
    onDragover(e) {
      e.preventDefault();
      e.stopPropagation();
      e.target.parentElement.parentElement.parentElement.classList.add(
        "topic-tree-hover"
      );
    },
    onDragLeave(e) {
      e.preventDefault();
      e.stopPropagation();
      e.target.parentElement.parentElement.parentElement.classList.remove(
        "topic-tree-hover"
      );
    },
    async onDrop(e, topic) {
      e.target.parentElement.parentElement.parentElement.classList.remove(
        "topic-tree-hover"
      );
      const item = JSON.parse(e.dataTransfer.getData("text/plain"));

      await update(item.id, {
        ...item,
        topics: collect(item.topics)
          .push({ id: topic.id })
          .unique("id")
          .values()
          .toArray(),
      });

      bus.$emit("alert-message", {
        type: "success",
        message: this.$t("items.assignTopics.Added item to topic successfully"),
      });

      await this.fetchItems();
    },
    async onDetachedTopic(item) {
      this.editedItem = item;
      await this.refreshItems();
    },
    fetchItems: debounce(async function () {
      try {
        this.loading = true;
        const options = {
          itemsPerPage: this.itemPagination?.itemsPerPage ?? 10,
          page: this.itemPagination?.page ?? 1,
          noTopic: this.showNonClassified ? 1 : 0,
        };

        const { data, pagination } = await getItems(
          this.course.id,
          this.search,
          options
        );
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
      await this.fetchItems();
    },
    displayItem: debounce(async function (item) {
      this.editedIndex = this.items.indexOf(item);
      if (!item.previewLoaded) {
        const { data } = await getItemByQuestionNumber(
          this.course.id,
          item.questionNumber
        );
        Object.assign(item, data, {
          previewLoaded: true,
        });
      }

      this.editedItem = Object.assign({}, item);
    }, 50),
    setPreviewResizeEvent() {
      const el = this.$refs.previewDivider;
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
    },
    setTopicResizeEvent() {
      const el = this.$refs.topicDivider;
      const vm = this;

      function resize(e) {
        document.body.style.cursor = "ew-resize";
        const offsetX = e.pageX - (vm.$store.state.drawer ? 275 : 0);
        if (offsetX > window.innerWidth - 400) {
          return;
        }
        vm.topicTreeWidth = offsetX < 0 ? 0 : offsetX;
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
    },
    setEvent() {
      this.setPreviewResizeEvent();
      this.setTopicResizeEvent();
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
  async mounted() {
    this.setEvent();
  },
};
</script>

<style scoped>
header {
  max-height: 60px;
}

.content-height {
  height: calc(100vh - 110px);
  overflow: auto;
}

.white-space-normal {
  white-space: normal;
}
</style>
