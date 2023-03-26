<template>
  <v-navigation-drawer
    :style="{ 'margin-top': $vuetify.breakpoint.mdAndDown ? null : '48px' }"
    fixed
    v-model="drawer"
  >
    <template v-if="course">
      <v-list-item :to="{ name: 'courses.list' }" exact>
        <v-list-item-icon>
          <v-icon>mdi-arrow-left</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("leftMenu.ReturnToCourseList") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-divider></v-divider>
      <v-menu v-if="course" offset-x :close-on-content-click="false">
        <template v-slot:activator="{ on, attrs }">
          <v-list-item two-line v-bind="attrs" v-on="on">
            <v-list-item-content>
              <v-list-item-title>
                {{ course.name }}
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ $t("leftMenu.CurrentCourse") }}
              </v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-action>
              <v-icon>mdi-menu-down</v-icon>
            </v-list-item-action>
          </v-list-item>
        </template>

        <v-list>
          <v-list-item>
            <v-list-item-content>
              <v-text-field
                v-model="courseSearch"
                :label="$t('leftMenu.Search Course')"
                outlined
                hide-details
                clearable
              >
              </v-text-field>
            </v-list-item-content>
          </v-list-item>
          <v-divider></v-divider>
          <template v-if="searchCourses.length > 0">
            <v-list-item
              v-for="item in searchCourses"
              :key="`${item.id}_course_left_menu`"
              link
              :href="`/courses/${item.id}/items`"
            >
              <v-list-item-content>
                <v-list-item-title>{{ item.name }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </template>
          <v-list-item v-else>
            <v-list-item-content>
              <v-list-item-title class="text-body-2 text-center">
                {{ $t("$vuetify.dataIterator.noResultsText") }}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>
    <v-divider></v-divider>
    <v-list>
      <v-list-item :to="{ name: 'items.list' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("items.board.topMenu") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item :to="{ name: 'items.import' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("items.import.topMenu") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item :to="{ name: 'items.config.import' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("items.board.Item Parameters Import") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item :to="{ name: 'items.assign-topic' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("items.assignTopics.topMenu") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item :to="{ name: 'topics.list' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("topics.topMenu") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item :to="{ name: 'topics.import' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("topics.Topics Import") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item :to="{ name: 'papers.list' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("papers.topMenu") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>

      <v-list-item :to="{ name: 'auto-papers.generator' }" exact>
        <v-list-item-content>
          <v-list-item-title>
            {{ $t("autoPapers.topMenu") }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import bus from "@/components/menus/bus";
import { getCourses } from "@/services/courseApi";

export default {
  name: "LeftMenu",
  props: {
    course: {
      type: [Object, null],
    },
  },
  data() {
    return {
      courseSearch: null,
      courses: [],
    };
  },
  computed: {
    searchCourses() {
      if (this.courseSearch) {
        return this.courses.filter((item) =>
          item.name.toLowerCase().includes(this.courseSearch.toLowerCase())
        );
      }
      return this.courses;
    },
    drawer: {
      set(value) {
        this.$store.commit("setDrawer", value);
      },
      get() {
        return this.$store.state.drawer;
      },
    },
  },
  methods: {
    toggle() {
      this.drawer = !this.drawer;
    },
    async fetchCourses() {
      const { data } = await getCourses(this.courseSearch || "", {
        page: 1,
        itemsPerPage: 9999,
      });

      this.courses = data;
    },
  },
  mounted() {
    this.drawer = this.$vuetify.breakpoint.lgAndUp;
    this.fetchCourses();
    bus.$on("toggle", this.toggle);
  },
};
</script>

<style scoped lang="scss"></style>
