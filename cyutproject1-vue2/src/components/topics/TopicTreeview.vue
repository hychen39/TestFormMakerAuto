<template>
  <div v-if="ready">
    <v-treeview
      v-if="topicTreeItems.length > 0"
      :items="topicTreeItems"
      :active.sync="_active"
      :open.sync="_open"
      open-all
      activatable
      return-object
    >
      <template v-slot:label="{ item }">
        {{ item.user_topic_id }} {{ item.name }}
        <span v-if="!item.id" class="text-caption">(unsaved)</span>
      </template>
      <template v-slot:append="{ item }">
        <slot name="append" :item="item"></slot>
      </template>
    </v-treeview>
    <v-alert v-else color="info" dark>
      {{ $t("topics.No topics, please add new topic") }}
    </v-alert>
  </div>
</template>

<script>
import { getTopics } from "@/services/TopicApi";

export default {
  name: "TopicTreeview",
  props: {
    course: {
      type: Object,
      required: true,
    },
    active: {
      type: Array,
    },
    open: {
      type: Array,
    },
  },
  data() {
    return {
      ready: false,
    };
  },
  asyncComputed: {
    topicTreeItems: {
      async get() {
        const data = await getTopics(this.course.id);
        setTimeout(() => {
          this.ready = true;
        }, 300);
        return data;
      },
      default: [],
    },
  },
  computed: {
    _active: {
      set(value) {
        this.$emit("update:active", value);
      },
      get() {
        return this.active;
      },
    },
    _open: {
      set(value) {
        this.$emit("update:open", value);
      },
      get() {
        return this.open;
      },
    },
  },
  methods: {
    openAll() {
      this._open = this.topicTreeItems;
    },
    closeAll() {
      this._open = [];
    },
    reload() {
      this.$asyncComputed.topicTreeItems.update();
    },
  },
};
</script>

<style scoped></style>
