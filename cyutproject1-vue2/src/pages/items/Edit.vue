<template>
  <v-container fluid>
    <ValidationObserver v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(onSubmit)">
        <v-card class="mt-3">
          <v-app-bar dark color="#555555">
            <v-toolbar-title>
              <span> {{ $t("Edit") }} {{ $t("items.Item") }} </span>
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-dialog v-model="deleteItemConfirmation" max-width="500">
              <template v-slot:activator="{ on, attrs }">
                <v-btn color="error" v-bind="attrs" v-on="on" class="mr-3">
                  {{ $t("Delete") }} {{ $t("items.Item") }}
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5">
                  {{ $t("items.Are you sure to deleting the item?") }}
                </v-card-title>
                <v-card-text>
                  {{ $t("items.This will be permanent") }}</v-card-text
                >
                <v-card-actions>
                  <v-spacer></v-spacer>

                  <v-btn
                    v-if="!deleteItemLoading"
                    text
                    @click="deleteItemConfirmation = false"
                  >
                    {{ $t("Cancel") }}
                  </v-btn>

                  <v-btn
                    color="primary"
                    @click="onDeletedItemConfirmation"
                    :loading="deleteItemLoading"
                  >
                    {{ $t("Yes, Delete") }}
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <v-btn
              v-if="!loading"
              :to="{ name: 'items.list', query: $route.query }"
              color="teal"
            >
              {{ $t("Cancel") }}
            </v-btn>
            <v-btn
              :loading="loading"
              color="primary"
              type="submit"
              class="ml-3"
            >
              {{ $t("Submit") }}
            </v-btn>
          </v-app-bar>
          <v-card-text
            class="overflow-auto pb-0"
            style="height: calc(100vh - 134px)"
          >
            <fields v-if="form.id" :form="form"> </fields>
          </v-card-text>
        </v-card>
      </v-form>
    </ValidationObserver>
  </v-container>
</template>

<script>
import { destroy, getItemByQuestionNumber, update } from "@/services/ItemApi";
import bus from "@/components/core/bus";
import form from "@/components/items/form";
import Fields from "@/components/items/Fields.vue";

export default {
  name: "Edit",
  mixins: [form],
  components: {
    Fields,
  },
  props: {
    course: {
      type: Object,
      required: true,
    },
    questionNumber: {
      type: [String],
      required: true,
    },
  },
  data() {
    return {
      loading: false,
      deleteItemConfirmation: false,
      deleteItemLoading: false,
    };
  },
  methods: {
    async onDeletedItemConfirmation() {
      try {
        this.deleteItemLoading = true;
        await destroy(this.form.id);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("items.Deleted item successfully"),
        });
        await this.$router.replace({
          name: "items.list",
          query: this.$route.query,
        });
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.deleteItemLoading = false;
      }
      this.deleteItemConfirmation = false;
    },
    async fetch() {
      try {
        const { data } = await getItemByQuestionNumber(
          this.course.id,
          this.questionNumber
        );
        this.form = data;
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      }
    },
    async onSubmit() {
      try {
        this.loading = true;
        await update(this.form.id, this.form);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("items.Updated successfully"),
        });
        await this.$router.replace({
          name: "items.list",
          query: this.$route.query,
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
  async mounted() {
    await this.fetch();
  },
};
</script>

<style scoped></style>
