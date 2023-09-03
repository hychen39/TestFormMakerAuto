import { getItemByQuestionNumber } from "@/services/ItemApi";

export default {
  methods: {
    async previewItemOnPrimeVueTable({ data }) {
      if (!data.content) {
        const response = await getItemByQuestionNumber(
          this.course.id,
          data.questionNumber
        );
        Object.assign(data, response.data, {
          previewLoaded: true,
        });
      }
    },
    async onPreviewItem({ item, value }) {
      if (value && !item.content) {
        const { data } = await getItemByQuestionNumber(
          this.course.id,
          item.questionNumber
        );
        Object.assign(item, data, {
          previewLoaded: true,
        });
      }
    },
  },
};
