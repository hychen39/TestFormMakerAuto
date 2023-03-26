<template>
  <div>
    <v-card flat v-if="item" :loading="loading">
      <v-card-title>
        <span>{{ item.title }}</span>
        <v-spacer></v-spacer>
        <v-btn v-if="closeable" fab icon small @click="onClose">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-divider></v-divider>
      <v-subheader>Topics</v-subheader>
      <v-card-text>
        <v-chip
          v-for="topic in item.topics"
          :key="`edit_topic_${topic.id}`"
          class="ma-1"
          :close="topicDetachable"
          @click:close="onDetachTopic(topic)"
        >
          {{ topic.user_topic_id }} {{ topic.name }}
        </v-chip>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-text class="mt-3">
        <vue-markdown :source="item.content"></vue-markdown>
      </v-card-text>
    </v-card>
    <v-card flat v-else>
      <v-alert color="info" text>
        {{ $t("items.previewCard.Click item") }}
        <v-icon class="mx-2" color="info">mdi-eye</v-icon>
        {{ $t("items.previewCard.to preview") }}
      </v-alert>
    </v-card>
  </div>
</template>

<script>
import VueMarkdown from "@adapttive/vue-markdown";
import { update } from "@/services/ItemApi";
import collect from "collect.js";
import bus from "@/components/core/bus";

export default {
  name: "PreviewCard",
  components: {
    VueMarkdown,
  },
  props: {
    item: {
      type: Object,
    },
    closeable: {
      type: Boolean,
      default() {
        return false;
      },
    },
    topicDetachable: {
      type: Boolean,
      default() {
        return false;
      },
    },
  },
  emits: ["close", "detachedTopic"],
  data() {
    return {
      loading: false,
    };
  },
  methods: {
    onClose() {
      this.$emit("close");
    },
    async onDetachTopic(topic) {
      try {
        this.loading = true;
        const item = await update(this.item.id, {
          ...this.item,
          topics: collect(this.item.topics)
            .reject((item) => item.id === topic.id)
            .values()
            .all(),
        });
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("items.previewCard.Removed topic successfully"),
        });

        this.$emit("detachedTopic", item);
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.response.data.message || e.message,
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped></style>
