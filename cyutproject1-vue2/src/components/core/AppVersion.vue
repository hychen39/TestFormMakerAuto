<template>
  <span class="text-caption">{{ version }} - {{ $t("project.title") }} </span>
</template>

<script>
export default {
  name: "AppVersion",
  computed: {
    version() {
      return process.env.VUE_APP_VERSION;
    },
  },
};
</script>

<style scoped></style>
