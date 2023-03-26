<template>
  <v-main>
    <v-container>
      <ValidationObserver v-slot="{ handleSubmit }">
        <v-form @submit.prevent="handleSubmit(onSubmit)">
          <v-card>
            <v-app-bar dark color="#555555">
              <v-toolbar-title>
                {{ $t("Edit") }} {{ $t("courses.Course") }}
              </v-toolbar-title>
              <v-spacer></v-spacer>
            </v-app-bar>
            <v-card-text>
              <fields :form="form"></fields>
            </v-card-text>
            <v-card-actions>
              <v-dialog v-model="deleteCourseConfirmation" max-width="500">
                <template v-slot:activator="{ on, attrs }">
                  <v-btn color="error" text v-bind="attrs" v-on="on">
                    {{ $t("Delete") }} {{ $t("courses.Course") }}
                  </v-btn>
                </template>
                <v-card>
                  <v-card-title class="text-h5">
                    {{ $t("courses.Are you sure to delete the course?") }}
                  </v-card-title>
                  <v-card-text>
                    {{ $t("courses.This will be permanent") }}</v-card-text
                  >
                  <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                      v-if="!deleteCourseLoading"
                      text
                      @click="deleteCourseConfirmation = false"
                    >
                      {{ $t("Cancel") }}
                    </v-btn>

                    <v-btn
                      color="primary"
                      @click="onDeleteCourse"
                      :loading="deleteCourseLoading"
                    >
                      {{ $t("Yes, Delete") }}
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
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
import { deleteCourse, getCourse, updateCourse } from "@/services/courseApi";

export default {
  name: "Edit",
  components: {
    Fields,
  },
  mixins: [form],
  props: {
    editCourseId: {
      type: [String, Number],
    },
  },
  data() {
    return {
      loading: false,
      deleteCourseConfirmation: false,
      deleteCourseLoading: false,
    };
  },
  methods: {
    async onSubmit() {
      try {
        this.loading = true;
        await updateCourse(this.editCourseId, this.form);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("courses.Updated course successfully"),
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
    async onDeleteCourse() {
      try {
        this.deleteCourseLoading = true;

        await deleteCourse(this.editCourseId);

        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("courses.Deleted course successfully"),
        });
        await this.$router.push({ name: "courses.list" });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.response.data.message || e.message,
        });
      } finally {
        this.deleteCourseLoading = false;
      }
    },
    async fetch() {
      const { data } = await getCourse(this.editCourseId);
      this.form = data;
    },
  },
  async mounted() {
    await this.fetch();
  },
};
</script>

<style scoped></style>
