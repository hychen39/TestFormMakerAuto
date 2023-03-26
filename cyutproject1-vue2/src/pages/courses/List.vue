<template>
  <v-main>
    <v-container>
      <v-card>
        <v-app-bar dark color="#555555">
          <v-toolbar-title>
            {{ $t("courses.Course") }} {{ $t("List") }}
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn color="primary" :to="{ name: 'courses.create' }">
            {{ $t("Create") }} {{ $t("courses.Course") }}
          </v-btn>
        </v-app-bar>

        <v-card-text>
          <v-data-table
            :headers="headers"
            :items="items"
            :loading="loading"
            :options.sync="coursesPagination"
            :items-per-page="10"
            :server-items-length="totalItems"
            :footer-props="{ 'items-per-page-options': [10, 50, 100, -1] }"
            disable-sort
          >
            <template v-slot:item.name="{ item }">
              <v-btn
                :to="{ name: 'items.list', params: { courseId: item.id } }"
                text
              >
                {{ item.name }}
              </v-btn>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                icon
                :to="{
                  name: 'courses.edit',
                  params: { editCourseId: item.id },
                }"
              >
                <v-icon> mdi-pencil</v-icon>
              </v-btn>
            </template>
          </v-data-table>
        </v-card-text>
      </v-card>
    </v-container>
  </v-main>
</template>

<script>
import { getCourses } from "@/services/courseApi";
import { debounce } from "vue-debounce";
import bus from "@/components/core/bus";

export default {
  name: "List",
  data() {
    return {
      loading: false,
      search: "",
      items: [],
      coursesPagination: {},
      totalItems: 1,
    };
  },
  computed: {
    headers() {
      return [
        { text: this.$t("courses.ID"), value: "id" },
        { text: this.$t("courses.Name"), value: "name" },
        { text: this.$t("courses.ItemCount"), value: "itemCount" },
        { text: this.$t("courses.PaperCount"), value: "paperCount" },
        { text: this.$t("courses.Actions"), value: "actions" },
      ];
    },
  },
  watch: {
    coursesPagination: {
      handler() {
        this.fetch();
      },
      deep: true,
    },
    search() {
      this.coursesPagination.page = 1;
      this.fetch();
    },
  },
  methods: {
    fetch: debounce(async function () {
      try {
        this.loading = true;
        const { data, pagination } = await getCourses(
          this.search,
          this.coursesPagination
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
  },
  created() {
    this.$store.commit("setCurrentCourse", null);
  },
};
</script>

<style scoped></style>
