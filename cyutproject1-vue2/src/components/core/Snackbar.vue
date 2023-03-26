<template>
  <v-snackbar
    :left="true"
    :bottom="true"
    :multi-line="true"
    :color="type"
    :timeout="-1"
    v-model="value"
  >
    <span v-html="message"></span>

    <template v-slot:action="{ attrs }">
      <v-btn color="white" text v-bind="attrs" @click="value = false">
        <v-icon> mdi-close </v-icon>
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>
import bus from "@/components/core/bus";

export default {
  name: "Snackbar",
  data() {
    return {
      type: "info",
      value: false,
      message: null,
    };
  },
  mounted() {
    bus.$on("alert-message", ({ type, message }) => {
      this.type = type;
      this.message = message;
      this.value = true;
    });
  },
};
</script>

<style scoped></style>
