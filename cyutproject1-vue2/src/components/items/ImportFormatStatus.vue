<template>
  <div class="d-flex align-center">
    <v-icon v-if="errors" :color="errors.length > 0 ? 'warning' : 'success'">
      {{ errors.length > 0 ? "mdi-alert-circle" : "mdi-check-circle" }}
    </v-icon>
    <span class="ml-3" v-for="error in errors" :key="error">
      {{ error }}
    </span>
  </div>
</template>
<script>
export default {
  props: {
    errors: {
      type: Array,
      required: true,
    },
  },
};
</script>

<style scoped></style>
