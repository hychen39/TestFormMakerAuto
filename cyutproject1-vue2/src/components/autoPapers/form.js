import { format } from "date-fns";

export default {
  data() {
    return {
      form: {
        title: null,
        summary: null,
        date: format(new Date(), "Y-MM-dd"),
        items: [],
      },
    };
  },
};
