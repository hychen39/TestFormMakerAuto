<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("topics.Import Topics") }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
      </v-app-bar>
      <v-card-text>
        <v-file-input
          :loading="importing"
          accept=".csv"
          :label="$t('topics.Import Topics') + ' (csv)'"
          show-size
          small-chips
          v-model="file"
          color="#7199de"
          @change="importTopics"
        >
        </v-file-input>
      </v-card-text>
    </v-card>

    <v-expand-transition>
      <v-card
        v-if="(invalidIds && invalidIds.length > 0) || error"
        class="mt-3"
        flat
      >
        <v-card-text class="px-0">
          <v-alert color="error" dark>
            <div class="title">{{ $t("topics.Import Failed") }}</div>
            <div class="mb-3">
              {{ invalidText || error }}
            </div>
            <div>
              <v-chip
                v-for="(invalidId, key) in invalidIds"
                :key="invalidId"
                :class="{ 'mx-3': key > 0, 'mr-3': key === 0 }"
                color="white"
                outlined
              >
                {{ invalidId }}
              </v-chip>
            </div>
          </v-alert>
        </v-card-text>
      </v-card>

      <v-card v-if="success" class="mt-3" flat>
        <v-card-text class="px-0">
          <v-alert color="success" dark>
            <div class="title">
              {{ $t("topics.Import topics successfully") }}
            </div>
            <div class="mt-3">
              <v-btn :to="{ name: 'topics.list' }" exact outlined>
                {{ $t("topics.Go To Topics Page") }}
              </v-btn>
            </div>
          </v-alert>
        </v-card-text>
      </v-card>
    </v-expand-transition>
  </v-container>
</template>

<script>
import * as papa from "papaparse";
import { readFile } from "@/services/fileReader";
import collect from "collect.js";
import { importTopics } from "@/services/TopicApi";

export default {
  name: "Import",
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      file: null,
      invalidIds: null,
      invalidText: null,
      importing: false,
      error: null,
      success: false,
    };
  },
  methods: {
    async importTopics() {
      if (!this.file) {
        return;
      }
      this.success = false;
      this.error = null;
      this.invalidIds = null;
      try {
        const csv = papa.parse(await readFile(this.file), {
          header: true,
          skipEmptyLines: true,
        });

        if (csv.data.length > 0) {
          const topics = collect(csv.data)
            .map((topic) => {
              const userTopicId = topic["User Topic ID"].trim();
              const userTopicIdFormatted = userTopicId.split(".");
              return {
                name: topic["Name"].trim(),
                user_topic_id: userTopicId,
                user_parent_id:
                  userTopicIdFormatted.length === 2
                    ? userTopicIdFormatted[0].trim()
                    : null,
              };
            })
            .all();
          await importTopics(this.course.id, topics);
          this.success = true;
        }
      } catch (e) {
        if (e.response?.status === 400) {
          if (e.response.data.data?.user_topic_ids?.length > 0) {
            this.invalidIds = e.response.data.data?.user_topic_ids || [];
            if (
              e.response.data.exception_message?.indexOf("does not exist") > 0
            ) {
              this.invalidText = this.$t(
                "topics.Found invalid parent topic id, please double check data"
              );
            } else {
              this.invalidText = this.$t(
                "topics.Found duplicates, please double check data"
              );
            }
            return;
          }
        }

        this.error = e.message;
      } finally {
        this.file = null;
      }
    },
  },
};
</script>

<style scoped></style>
