<template>
  <v-container fluid>
    <ValidationObserver v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(onSubmit)">
        <v-card>
          <v-app-bar dark color="#555555">
            <v-toolbar-title>
              {{ $t("autoPapers.Auto Paper") }}
            </v-toolbar-title>
            <v-spacer></v-spacer>
          </v-app-bar>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4">
                <v-row>
                  <v-col cols="12">
                    <fields :form="form"></fields>
                  </v-col>
                  <v-col cols="12">
                    <v-subheader class="px-0">
                      {{ $t("autoPapers.Config") }}
                    </v-subheader>
                    <v-row>
                      <v-col cols="12" sm="6">
                        <v-text-field-with-validation
                          :label="$t('autoPapers.Cutoff Low Bound')"
                          v-model="lower_target"
                          type="number"
                          rules="required|between:0,1"
                        >
                        </v-text-field-with-validation>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field-with-validation
                          :label="$t('autoPapers.Cutoff Score')"
                          v-model="target"
                          type="number"
                          rules="required|between:0,1"
                        >
                        </v-text-field-with-validation>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field-with-validation
                          :label="$t('autoPapers.Cutoff Upper Bound')"
                          v-model="upper_target"
                          type="number"
                          rules="required|between:0,1"
                        >
                        </v-text-field-with-validation>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field-with-validation
                          :label="$t('autoPapers.False Rate')"
                          v-model="setP"
                          type="number"
                          rules="required|between:0,1"
                        >
                        </v-text-field-with-validation>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field-with-validation
                          :label="$t('autoPapers.Minimum Items')"
                          v-model="minimum_items"
                          type="number"
                          rules="required"
                        >
                        </v-text-field-with-validation>
                      </v-col>
                    </v-row>
                  </v-col>
                </v-row>
              </v-col>
              <v-divider
                v-if="$vuetify.breakpoint.mdAndUp"
                vertical
              ></v-divider>
              <v-col cols="12" md="8">
                <div v-if="step === 0">
                  <v-subheader>
                    {{ $t("autoPapers.Topics") }}
                  </v-subheader>
                  <div
                    :class="{
                      'topic-container': $vuetify.breakpoint.mdAndUp,
                    }"
                  >
                    <v-treeview
                      v-model="selectedTopics"
                      :items="topicTreeItems"
                      selection-type="leaf"
                      return-object
                      selectable
                    >
                      <template v-slot:label="{ item }">
                        <div class="white-space-normal my-2">
                          {{ item.user_topic_id }} {{ item.name }}
                          <span>
                            {{ getItemCount(item.id) }}
                          </span>
                        </div>
                      </template>
                    </v-treeview>
                  </div>
                </div>

                <div v-if="step === 1">
                  <v-subheader>
                    <span>
                      {{
                        $t(
                          "autoPapers.Suggested Max. Misclassification Probability"
                        )
                      }}:
                      <b>
                        {{ bestRate }}
                      </b>
                    </span>
                  </v-subheader>
                  <div
                    :class="{
                      'topic-container': $vuetify.breakpoint.mdAndUp,
                    }"
                  >
                    <v-data-table
                      :headers="headers"
                      :items="pickedItems"
                      disable-pagination
                      disable-sort
                      hide-default-footer
                    >
                      <template v-slot:item.items="{ item }">
                        <v-btn
                          color="primary"
                          text
                          @click.prevent="onPreview(item)"
                        >
                          {{ item.items.length }}
                        </v-btn>
                      </template>
                      <template v-slot:item.wrongRate="{ item }">
                        <span>
                          {{ item.wrongRate }}
                        </span>
                        <v-chip v-if="item.best" small label color="success">
                          {{ $t("autoPapers.Best") }}
                        </v-chip>
                      </template>

                      <template v-slot:item.action="{ item }">
                        <v-btn
                          color="primary"
                          @click.prevent="onSelected(item)"
                          :loading="loading"
                        >
                          {{ $t("Create") }}
                        </v-btn>
                      </template>
                    </v-data-table>
                  </div>
                </div>
              </v-col>
            </v-row>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              v-if="step === 0"
              color="primary"
              type="submit"
              :loading="loading"
            >
              {{ $t("autoPapers.Generate") }}
            </v-btn>
            <v-btn
              v-if="step === 1"
              color="primary"
              @click.prevent="onPrevious"
              :loading="loading"
            >
              {{ $t("autoPapers.Reset") }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-form>
    </ValidationObserver>

    <select-item-preview v-model="previewItems" :course="course">
    </select-item-preview>
  </v-container>
</template>

<script>
import MasterExamApi from "@/services/MasterExamApi";
import { getItemsByTopicIds } from "@/services/TopicApi";
import form from "@/components/autoPapers/form";
import Fields from "@/components/autoPapers/Fields";
import bus from "@/components/core/bus";
import collect from "collect.js";
import { createPaper } from "@/services/paperApi";
import SelectItemPreview from "@/components/autoPapers/SelectItemPreview.vue";
import evaluatePaper from "@/components/papers/evaluatePaper";

export default {
  name: "Generator",
  mixins: [form, evaluatePaper],
  components: {
    SelectItemPreview,
    Fields,
  },
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      loading: false,
      lower_target: 0.7,
      target: 0.8,
      upper_target: 0.9,
      decimal: 3,
      setP: 0.01,
      minimum_items: window.AUTOPAPER_MINIMUM_ITEM,
      step: 0,
      pickedItems: [],
      bestRate: null,
      previewItems: null,
    };
  },
  methods: {
    onPreview(item) {
      this.previewItems = item.items;
    },
    onPrevious() {
      this.step = 0;
      this.pickedItems = [];
      this.bestRate = null;
    },
    async onSelected(item) {
      try {
        this.loading = true;
        this.form.items = collect(item.items).pluck("id").all();
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
    async onSubmit() {
      if (this.selectedTopics.length === 0) {
        return;
      }

      try {
        this.loading = true;
        const { data } = await getItemsByTopicIds(
          this.course.id,
          collect(this.selectedTopics).pluck("id").all()
        );

        if (data.length < this.minimum_items) {
          bus.$emit("alert-message", {
            type: "error",
            message: this.$t("Topics.Items were not enough"),
          });
          return;
        }

        const result = MasterExamApi.masteryExamAlg(
          data,
          this.lower_target,
          this.target,
          this.upper_target,
          3,
          this.setP,
          this.minimum_items
        );
        this.pickedItems = result.items;
        this.bestRate = result.bestRate;
        this.step = 1;
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
  computed: {
    headers() {
      return [
        {
          text: this.$t("autoPapers.Item Counts"),
          value: "items",
          sortable: false,
        },
        {
          text: this.$t("autoPapers.Cutoff Item Number"),
          value: "n0",
          sortable: false,
        },
        {
          text: this.$t("autoPapers.Misclassification Probability"),
          value: "wrongRate",
          sortable: false,
        },
        {
          text: this.$t("autoPapers.Max of falsely passing a non-master"),
          value: "lowerBound",
          sortable: false,
        },
        {
          text: this.$t("autoPapers.Max prob of falsely failing a master"),
          value: "upperBound",
          sortable: false,
        },
        {
          text: this.$t("autoPapers.Action"),
          value: "action",
          sortable: false,
        },
      ];
    },
  },
};
</script>

<style scoped>
.topic-container {
  overflow-y: auto;
  height: calc(100vh - 265px);
}
</style>
