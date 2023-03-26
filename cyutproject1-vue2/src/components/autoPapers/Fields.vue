<template>
  <v-row>
    <v-col cols="12">
      <v-text-field-with-validation
        name="title"
        :label="$t('autoPapers.Title')"
        v-model="form.title"
        rules="required"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12">
      <v-text-field-with-validation
        name="summary"
        :label="$t('autoPapers.Summary')"
        v-model="form.summary"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12">
      <v-menu
        v-model="openDate"
        :close-on-content-click="false"
        :nudge-right="40"
        transition="scale-transition"
        offset-y
        min-width="auto"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field-with-validation
            name="date"
            :label="$t('autoPapers.Date')"
            v-model="form.date"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
            rules="required"
          >
          </v-text-field-with-validation>
        </template>
        <v-date-picker
          v-model="form.date"
          @input="openDate = false"
        ></v-date-picker>
      </v-menu>
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
  data() {
    return {
      openDate: false,
    };
  },
};
</script>

<style scoped></style>
