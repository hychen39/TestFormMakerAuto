<template>
  <v-app>
    <top-menu></top-menu>
    <left-menu v-if="course" :course="course"></left-menu>
    <v-main
      :style="{
        'padding-left':
          isDrawerOpened && course && $vuetify.breakpoint.lgAndUp
            ? '256px'
            : null,
      }"
    >
      <router-view></router-view>
    </v-main>
    <snackbar></snackbar>
    <v-footer class="justify-end">
      <app-version></app-version>
    </v-footer>
  </v-app>
</template>

<script>
import Snackbar from "@/components/core/Snackbar";
import TopMenu from "@/components/menus/TopMenu";
import "@/assets/scss/app.scss";
import LeftMenu from "@/components/menus/LeftMenu.vue";
import AppVersion from "@/components/core/AppVersion.vue";

export default {
  name: "App",
  components: { Snackbar, TopMenu, LeftMenu, AppVersion },
  computed: {
    isPrint() {
      return this.$store.state.isPrint;
    },
    course() {
      return this.$store.state.currentCourse;
    },
    isDrawerOpened() {
      return this.$store.state.drawer;
    },
  },
  watch: {
    "$i18n.locale": {
      handler() {
        // this.$refs.tabs && this.$refs.tabs.onResize();
      },
    },
  },
};
</script>

<style scoped></style>
