import { getItemByQuestionNumber } from "@/services/ItemApi";

export default {
  methods: {
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
