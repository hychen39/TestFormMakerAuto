<template>
  <v-app-bar
    v-if="!isPrint"
    dense
    app
    flat
    color="white"
    class="v-bar--underline"
  >
    <v-app-bar-nav-icon
      v-if="shouldShowMenuIcon"
      @click="drawer = !drawer"
    ></v-app-bar-nav-icon>
    <v-toolbar-title class="text-subtitle-1 pl-0">
      {{ $t("project.title") }}
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-menu left bottom>
      <template v-slot:activator="{ on, attrs }">
        <v-btn icon v-bind="attrs" v-on="on">
          <v-icon>mdi-translate</v-icon>
        </v-btn>
      </template>

      <v-list>
        <v-subheader>
          {{ $t("Translations") }}
        </v-subheader>
        <v-list-item-group v-model="$i18n.locale" color="primary" mandatory>
          <v-list-item value="en">
            <v-list-item-title>
              {{ $t("langs.English") }}
            </v-list-item-title>
          </v-list-item>
          <v-list-item value="zhHant">
            <v-list-item-title>
              {{ $t("langs.zhHant") }}
            </v-list-item-title>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-menu>
  </v-app-bar>
</template>

<script>
export default {
  name: "TopMenu",
  computed: {
    isPrint() {
      return this.$store.state.isPrint;
    },
    shouldShowTheTitle() {
      if (!this.drawer) {
        return true;
      }
      if (this.$vuetify.breakpoint.mdAndDown) {
        return true;
      }

      return !this.$route.params.courseId;
    },
    shouldShowMenuIcon() {
      return this.$route.params.courseId;
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
};
</script>

<style scoped></style>
