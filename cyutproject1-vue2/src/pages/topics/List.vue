<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("topics.Topics") }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="$refs.topicTreeview.openAll()">
          {{ $t("Topics.Open All") }}
        </v-btn>
        <v-btn
          color="primary"
          class="mx-3"
          @click="$refs.topicTreeview.closeAll()"
        >
          {{ $t("Topics.Close All") }}
        </v-btn>
        <v-dialog max-width="400" v-model="dialog">
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" v-bind="attrs" v-on="on">
              <v-icon>mdi-plus</v-icon>
              {{ $t("topics.Create Topic") }}
            </v-btn>
          </template>
          <ValidationObserver v-slot="{ handleSubmit }">
            <v-form @submit.prevent="handleSubmit(submitCreateTopic)">
              <v-card>
                <v-card-title>
                  {{ $t("topics.Create Topic") }}
                </v-card-title>
                <v-card-text>
                  <v-text-field-with-validation
                    name="name"
                    :label="$t('topics.Name')"
                    v-model="form.name"
                    rules="required"
                  >
                  </v-text-field-with-validation>
                  <v-text-field-with-validation
                    name="user_topic_id"
                    :label="$t('topics.Topic User ID')"
                    v-model="form.user_topic_id"
                    rules="required"
                  >
                  </v-text-field-with-validation>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn v-if="!loading" @click="cancelCreateTopic" text plain>
                    {{ $t("Cancel") }}
                  </v-btn>
                  <v-btn :loading="loading" color="primary" type="submit">
                    {{ $t("Submit") }}
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-form>
          </ValidationObserver>
        </v-dialog>
      </v-app-bar>
      <v-card-text>
        <v-row style="max-height: calc(100vh - 145px); overflow-y: auto">
          <v-col cols="12">
            <topic-treeview
              ref="topicTreeview"
              :course="course"
              :active.sync="active"
              :open.sync="open"
            >
              <template v-slot:append="{ item }">
                <v-btn
                  v-if="!item.parent_id"
                  icon
                  @click="(e) => onAddChildTopic(e, item)"
                >
                  <v-icon> mdi-plus</v-icon>
                </v-btn>
              </template>
            </topic-treeview>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <v-dialog :value="active.length > 0" max-width="400">
      <v-card>
        <v-card-title>
          <span v-if="active.length > 0 && active[0].id">
            {{ $t("topics.Update Topic") }}
          </span>
          <span v-else>
            {{ $t("topics.Add Subtopic") }}
          </span>
        </v-card-title>
        <v-card-text class="mt-3">
          <fields
            v-if="active.length > 0"
            :form="active[0]"
            :course="course"
            @saved="onSave"
            @cancel="onCancel"
          ></fields>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import { createTopic } from "@/services/TopicApi";
import Fields from "@/components/topics/Fields.vue";
import bus from "@/components/core/bus";
import TopicTreeview from "@/components/topics/TopicTreeview.vue";

const defaultForm = {
  name: null,
  user_topic_id: null,
};

export default {
  name: "List",
  components: {
    Fields,
    TopicTreeview,
  },
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      active: [],
      parentActive: null,
      open: [],
      loading: false,
      dialog: false,
      search: "",
      ready: false,
      form: Object.assign({}, defaultForm),
    };
  },
  methods: {
    cancelCreateTopic() {
      this.dialog = false;
      this.form = Object.assign({}, defaultForm);
    },
    async submitCreateTopic() {
      try {
        this.loading = true;
        await createTopic(this.course.id, this.form);
        // call reload
        this.$refs.topicTreeview.reload();
        this.dialog = false;
        this.form = Object.assign({}, defaultForm);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("topics.Created topic successfully"),
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
    onSave() {
      this.$refs.topicTreeview.reload();
      this.active = [];
    },
    onCancel() {
      if (!this.active[0].id) {
        const index = this.parentActive.children.indexOf(this.active[0]);
        this.parentActive.children.splice(index, 1);
      }

      this.active = [];
      this.parentActive = null;
    },
    onAddChildTopic(e, item) {
      e.preventDefault();
      e.stopPropagation();
      const newItem = {
        name: "New Topic",
        parent_id: item.id,
        user_topic_id: null,
      };
      item.children.push(newItem);
      this.parentActive = item;
      this.active = [newItem];
    },
  },
};
</script>

<style scoped></style>
