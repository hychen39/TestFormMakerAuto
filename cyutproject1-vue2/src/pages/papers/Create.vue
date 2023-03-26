<template>
  <v-container fluid>
    <ValidationObserver v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(onSubmit)">
        <v-card>
          <v-app-bar dark color="#555555">
            <v-toolbar-title>
              {{ $t("Create") }} {{ $t("papers.Paper") }}
            </v-toolbar-title>
            <v-spacer></v-spacer>
          </v-app-bar>
          <v-card-text>
            <fields :form="form"></fields>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn v-if="!loading" :to="{ name: 'papers.list' }" text plain>
              {{ $t("Cancel") }}
            </v-btn>
            <v-btn :loading="loading" color="primary" type="submit">
              {{ $t("Submit") }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-form>
    </ValidationObserver>
  </v-container>
</template>

<script>
import form from "@/components/papers/form";
import Fields from "@/components/papers/Fields";
import { createPaper } from "@/services/paperApi";
import bus from "@/components/core/bus";

export default {
  name: "Create",
  mixins: [form],
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  components: {
    Fields,
  },
  data() {
    return {
      loading: false,
    };
  },
  methods: {
    async onSubmit() {
      try {
        this.loading = true;
        const paper = await createPaper(this.course.id, this.form);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("papers.Created paper successfully"),
        });
        await this.$router.push({
          name: "papers.edit",
          params: { paperId: paper.id },
        });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped></style>
