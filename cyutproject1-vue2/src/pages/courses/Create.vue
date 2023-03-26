<template>
  <v-main>
    <v-container>
      <ValidationObserver v-slot="{ handleSubmit }">
        <v-form @submit.prevent="handleSubmit(onSubmit)">
          <v-card>
            <v-app-bar dark color="#555555">
              <v-toolbar-title>
                {{ $t("Create") }} {{ $t("courses.Course") }}
              </v-toolbar-title>
              <v-spacer></v-spacer>
            </v-app-bar>
            <v-card-text>
              <fields :form="form"></fields>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn v-if="!loading" :to="{ name: 'courses.list' }" text plain>
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
  </v-main>
</template>

<script>
import form from "@/components/courses/form";
import Fields from "@/components/courses/Fields";
import bus from "@/components/core/bus";
import { createCourse } from "@/services/courseApi";

export default {
  name: "Create",
  components: {
    Fields,
  },
  mixins: [form],
  data() {
    return {
      loading: false,
    };
  },
  methods: {
    async onSubmit() {
      try {
        this.loading = true;
        await createCourse(this.form);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("courses.Created course successfully"),
        });
        await this.$router.push({
          name: "courses.list",
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
