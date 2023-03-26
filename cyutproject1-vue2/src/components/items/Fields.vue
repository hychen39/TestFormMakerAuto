<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-text-field-with-validation
        name="title"
        :label="$t('items.Title')"
        v-model="form.title"
        rules="required"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12" md="6">
      <v-text-field-with-validation
        name="questionNumber"
        :label="$t('items.questionNumber')"
        v-model="form.questionNumber"
        rules="required"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12" md="6">
      <v-text-field-with-validation
        name="paramA"
        :label="$t('items.paramA')"
        v-model="form.a"
        rules="required|between:0,2"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12" md="6">
      <v-text-field-with-validation
        name="paramB"
        :label="$t('items.paramB')"
        v-model="form.b"
        rules="required|between:-3,3"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12" md="6">
      <v-text-field-with-validation
        name="paramC"
        :label="$t('items.paramC')"
        v-model="form.c"
        rules="required|between:0,1"
      >
      </v-text-field-with-validation>
    </v-col>
    <v-col cols="12" md="6">
      <v-text-field-with-validation
        name="maxInfoTheta"
        :label="$t('items.maxInfoTheta')"
        v-model="form.maxInfoTheta"
        readonly
      >
      </v-text-field-with-validation>
    </v-col>
    <v-subheader>{{ $t("items.Content") }}</v-subheader>
    <v-col cols="12">
      <v-md-editor v-model="form.content" height="400px"></v-md-editor>
    </v-col>
  </v-row>
</template>

<script>
import { round, sqrt } from "mathjs";
export default {
  name: "Fields",
  props: {
    form: {
      type: Object,
      required: true,
    },
  },
  watch: {
    "form.a": {
      handler(value) {
        this.form.a = round(value, 3);
        this.calculateMaxInfoTheta();
      },
    },
    "form.b": {
      handler(value) {
        this.form.b = round(value, 3);
        this.calculateMaxInfoTheta();
      },
    },
    "form.c": {
      handler(value) {
        this.form.c = round(value, 3);
        this.calculateMaxInfoTheta();
      },
    },
  },
  methods: {
    calculateMaxInfoTheta() {
      this.form.maxInfoTheta = round(
        this.form.b +
          (1 / 1.7) *
            this.form.a *
            Math.log(0.5 * (1 + sqrt(1 + 8 * this.form.c))),
        3
      );
    },
  },
};
</script>

<style scoped></style>
