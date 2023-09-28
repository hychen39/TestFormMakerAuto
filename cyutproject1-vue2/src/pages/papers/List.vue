<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("papers.Paper") }} {{ $t("List") }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <div class="d-flex flex-1-1-auto align-center justify-end">
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
          <v-btn color="primary" :to="{ name: 'papers.create' }">
            {{ $t("Create") }} {{ $t("papers.Paper") }}
          </v-btn>
        </div>
      </v-app-bar>
      <v-card-text style="overflow-y: auto; max-height: calc(100vh - 122px)">
        <v-data-table
          :headers="headers"
          :items="mapItems"
          :loading="loading"
          :options.sync="topicPagination"
          :items-per-page="10"
          :server-items-length="totalItems"
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
          <template v-slot:item.topics="{ item }">
            <v-chip
              v-for="topic in sortTopics(item.topics)"
              :key="`table_topic_${item.id}_${topic.id}`"
              class="ma-2"
            >
              {{ topic.user_topic_id }} {{ topic.name }}
            </v-chip>
          </template>
          <template v-slot:item.date="{ item }">
            {{ dateFormat(item.date) }}
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn
              icon
              :to="{ name: 'papers.edit', params: { paperId: item.id } }"
            >
              <v-icon> mdi-pencil </v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import { parseISO, format } from "date-fns";
import { getPapers } from "@/services/paperApi";
import bus from "@/components/core/bus";
import collect from "collect.js";
import { debounce } from "vue-debounce";
import ClientSortDataTable from "@/components/core/ClientSortDataTable.vue";

export default {
  name: "List",
  components: { ClientSortDataTable },
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      items: [],
      totalItems: null,
      loading: false,
      topicPagination: {},
      search: null,
      sortBy: [],
      sortDesc: [],
    };
  },
  computed: {
    headers() {
      return [
        { text: this.$t("papers.ID"), value: "id", width: "5%" },
        { text: this.$t("papers.Title"), value: "title" },
        { text: this.$t("papers.Date"), value: "date" },
        { text: this.$t("papers.Summary"), value: "summary" },
        {
          text: this.$t("papers.ItemCount"),
          value: "item_count",
          width: "10%",
        },
        {
          text: this.$t("papers.Topics"),
          value: "topics",
          width: "30%",
          sortable: false,
        },
        { text: this.$t("papers.Actions"), value: "actions", sortable: false },
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
  watch: {
    "topicPagination.page": {
      handler() {
        this.fetch();
      },
    },
    "topicPagination.itemsPerPage": {
      handler() {
        this.fetch();
      },
    },
    search() {
      this.topicPagination.page = 1;
      this.fetch();
    },
  },
  methods: {
    fetch: debounce(async function () {
      try {
        this.loading = true;
        const { data, pagination } = await getPapers(
          this.course.id,
          this.search,
          this.topicPagination
        );

        this.items = data;
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
    dateFormat(date) {
      if (date) {
        return format(parseISO(date), "yyyy-MM-dd");
      }
      return null;
    },
    sortTopics(topics) {
      return collect(topics).unique("name").sortBy("name").values().all();
    },
  },
};
</script>

<style scoped>
.flex-1-1-auto {
  flex: 1 1 auto;
}
</style>
