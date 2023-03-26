<template>
  <v-menu offset-y :close-on-content-click="false" max-width="600">
    <template v-slot:activator="{ on, attrs }">
      <v-btn v-bind="attrs" v-on="on" class="mx-3" color="warning">
        {{ $t("papers.evaluate missclass prob") }}
      </v-btn>
    </template>
    <v-card>
      <v-card-text>
        <v-row>
          <v-col cols="12" sm="4">
            <v-text-field-with-validation
              :label="$t('autoPapers.Cutoff Low Bound')"
              v-model="form.cutoffLowBound"
              type="number"
              rules="required|between:0,1"
            >
            </v-text-field-with-validation>
          </v-col>
          <v-col cols="12" sm="4">
            <v-text-field-with-validation
              :label="$t('autoPapers.Cutoff Score')"
              v-model="form.cutoffScore"
              type="number"
              rules="required|between:0,1"
            >
            </v-text-field-with-validation>
          </v-col>
          <v-col cols="12" sm="4">
            <v-text-field-with-validation
              :label="$t('autoPapers.Cutoff Upper Bound')"
              v-model="form.cutoffUpperBound"
              type="number"
              rules="required|between:0,1"
            >
            </v-text-field-with-validation>
          </v-col>
          <v-col cols="12" style="max-height: 500px; overflow-y: auto">
            <v-subheader>
              {{ $t("papers.Topics") }}
            </v-subheader>
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
          </v-col>
        </v-row>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions>
        <v-list
          dense
          v-if="form.cutoffNumber && form.maxMisclassificationProbValue"
        >
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>
                {{ form.cutoffNumber }}
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ $t("autoPapers.Cutoff Item Number") }}
              </v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-content>
              <v-list-item-title>
                {{ form.maxMisclassificationProbValue }}
              </v-list-item-title>
              <v-list-item-subtitle style="white-space: normal">
                {{ $t("papers.Max Misclassification Probability") }}
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="onCalculate" :disabled="!valid">
          {{ $t("papers.Calculate") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-menu>
</template>

<script>
import MasterExamApi from "@/services/MasterExamApi";
import evaluatePaper from "@/components/papers/evaluatePaper";
import collect from "collect.js";
import { getItemsByTopicIds } from "@/services/TopicApi";

export default {
  name: "CalculateMaxMisclassificationProbability",
  mixins: [evaluatePaper],
  props: {
    items: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      form: {
        cutoffLowBound: null,
        cutoffScore: null,
        cutoffUpperBound: null,
        maxMisclassificationProbValue: null,
        cutoffNumber: null,
      },
    };
  },
  computed: {
    valid() {
      if (
        !this.form.cutoffScore ||
        !this.form.cutoffLowBound ||
        !this.form.cutoffUpperBound
      ) {
        return false;
      }
      return (
        this.form.cutoffScore >= 0 &&
        this.form.cutoffLowBound >= 0 &&
        this.form.cutoffUpperBound >= 0 &&
        this.selectedTopics.length > 0
      );
    },
  },
  methods: {
    async onCalculate() {
      const { data } = await getItemsByTopicIds(
        this.course.id,
        collect(this.selectedTopics).pluck("id").all()
      );

      const { cutoffNumber, maxMisclassificationProbValue } =
        MasterExamApi.getMaxMisclassificationProbabilityValue(
          data,
          this.items,
          this.form.cutoffLowBound,
          this.form.cutoffScore,
          this.form.cutoffUpperBound,
          3
        );

      this.form.maxMisclassificationProbValue = maxMisclassificationProbValue;
      this.form.cutoffNumber = cutoffNumber;
    },
  },
};
</script>

<style scoped></style>
