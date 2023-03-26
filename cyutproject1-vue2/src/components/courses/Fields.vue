<template>
  <v-row>
    <v-col cols="12">
      <v-text-field-with-validation
        name="name"
        :label="$t('courses.Name')"
        v-model="form.name"
        rules="required"
      >
      </v-text-field-with-validation>
    </v-col>

    <v-col cols="12">
      <v-text-field-with-validation
        name="longDesc"
        :label="$t('courses.longDesc')"
        v-model="form.longDesc"
      >
      </v-text-field-with-validation>
    </v-col>
  </v-row>
</template>

<script>
export default {
  name: "Fields",
  props: {
    form: {
      type: Object,
      required: true,
    },
  },
};
</script>

<style scoped></style>
