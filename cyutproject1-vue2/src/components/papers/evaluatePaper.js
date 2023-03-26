import { getItemCountInTopic, getTopics } from "@/services/TopicApi";
import collect from "collect.js";

export default {
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      selectedTopics: [],
    };
  },
  asyncComputed: {
    topicTreeItems: {
      async get() {
        return await getTopics(this.course.id);
      },
      default: [],
    },
    itemCountDataset: {
      async get() {
        return (await getItemCountInTopic(this.course.id)).data;
      },
      default: [],
    },
  },
  methods: {
    getItemCount(topicId) {
      const item = collect(this.itemCountDataset).where("id", topicId).first();
      if (item) {
        return `(${item.item_count})`;
      }
      return null;
    },
  },
};
