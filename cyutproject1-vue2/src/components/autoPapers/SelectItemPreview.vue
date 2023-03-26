<template>
  <v-dialog v-model="dialog">
    <v-card>
      <v-card-title class="text-h5">
        <span>
          {{ $t("autoPapers.Preview Items") }}
        </span>
        <v-spacer></v-spacer>
        <v-btn icon color="primary" @click="dialog = false">
          <v-icon> mdi-close </v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text v-if="value">
        <v-data-table
          show-expand
          :items="value"
          item-key="questionNumber"
          :headers="headers"
          :expanded.sync="expanded"
          expand-icon="mdi-eye"
          disable-sort
          disable-pagination
          hide-default-footer
          @item-expanded="onPreviewItem"
        >
          <template v-slot:expanded-item="{ headers, item }">
            <td :colspan="headers.length">
              <preview-card :item="item" :closeable="false"></preview-card>
            </td>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import PreviewCard from "@/components/items/PreviewCard.vue";
import contentLazyLoad from "@/components/items/contentLazyLoad";

export default {
  name: "SelectItemPreview",
  components: { PreviewCard },
  mixins: [contentLazyLoad],
  props: {
    course: {
      type: Object,
      required: true,
    },
    value: {
      type: Array,
    },
  },
  data() {
    return {
      expanded: [],
    };
  },
  computed: {
    headers() {
      return [
        {
          text: this.$t("items.ID"),
          align: "start",
          sortable: true,
          value: "questionNumber",
        },
        { text: this.$t("items.Title"), value: "title" },
        { text: "a", value: "a" },
        { text: "b", value: "b" },
        { text: "c", value: "c" },
        { text: "θ_max", value: "maxInfoTheta" },
      ];
    },
    dialog: {
      set(value) {
        if (!value) {
          this.$emit("input", null);
        }
      },
      get() {
        return !!this.value;
      },
    },
  },
};
</script>

<style scoped></style>
