<template>
  <ValidationObserver v-slot="{ handleSubmit }">
    <v-form @submit.prevent="handleSubmit(onSubmit)">
      <v-row v-if="innerValue">
        <v-col cols="12">
          <v-textarea-with-validation
            name="name"
            :label="$t('topics.Name')"
            v-model="innerValue.name"
            rules="required"
          >
          </v-textarea-with-validation>
          <v-textarea-with-validation
            name="user_topic_id"
            :label="$t('topics.Topic User ID')"
            v-model="innerValue.user_topic_id"
            rules="required"
          >
          </v-textarea-with-validation>
        </v-col>
        <v-col class="d-flex" cols="12">
          <v-dialog
            v-if="innerValue.id"
            v-model="deleteConfirmation"
            max-width="500"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs" v-on="on" color="error" text>
                {{ $t("Delete") }}
              </v-btn>
            </template>
            <v-card>
              <v-card-title class="text-h5">
                {{ $t("topics.Are you sure to delete the topic?") }}
              </v-card-title>
              <v-card-text>
                <p>
                  {{ $t("topics.It’s all sub-topics will also be deleted") }}
                </p>
                <p>
                  {{ $t("topics.This will be permanent") }}
                </p>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>

                <v-btn v-if="!loading" text @click="deleteConfirmation = false">
                  {{ $t("Cancel") }}
                </v-btn>

                <v-btn
                  :loading="loading"
                  color="primary"
                  @click="onDeletedTopicConfirmation"
                >
                  {{ $t("Yes, Delete") }}
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>

          <v-spacer></v-spacer>
          <v-btn v-if="!loading" @click="onCancel" class="mr-3" text plain>
            {{ $t("Cancel") }}
          </v-btn>
          <v-btn :loading="loading" color="primary" type="submit">
            {{ $t("Submit") }}
          </v-btn>
        </v-col>
      </v-row>
    </v-form>
  </ValidationObserver>
</template>

<script>
import bus from "@/components/core/bus";
import { createTopic, deleteTopic, updateTopic } from "@/services/TopicApi";

export default {
  name: "Fields",
  props: {
    course: {
      type: Object,
      required: true,
    },
    form: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      loading: false,
      innerValue: null,
      deleteConfirmation: false,
    };
  },
  watch: {
    form: {
      handler(value) {
        this.innerValue = Object.assign({}, value);
      },
      deep: true,
    },
  },
  methods: {
    onCancel() {
      this.innerValue = null;
      this.$emit("cancel");
    },
    async onSubmit() {
      try {
        this.loading = true;
        if (this.innerValue.id) {
          const { data } = await updateTopic(
            this.innerValue.id,
            this.innerValue
          );
          bus.$emit("alert-message", {
            type: "success",
            message: this.$t("topics.Updated topic successfully"),
          });
          Object.assign(this.innerValue, { id: data.id });
        } else {
          const { data } = await createTopic(this.course.id, this.innerValue);
          bus.$emit("alert-message", {
            type: "success",
            message: this.$t("topics.Created topic successfully"),
          });
          Object.assign(this.innerValue, { id: data.id });
        }
        this.$emit("saved", this.innerValue);
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    },
    async onDeletedTopicConfirmation() {
      try {
        this.loading = true;
        await deleteTopic(this.form.id);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("topics.Deleted topic successfully"),
        });
        this.deleteConfirmation = false;
        this.$emit("saved", this.innerValue);
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
  mounted() {
    this.innerValue = Object.assign({}, this.form);
  },
};
</script>

<style scoped></style>
