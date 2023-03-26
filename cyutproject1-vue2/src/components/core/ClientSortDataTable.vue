<template>
  <thead class="v-data-table-header">
    <tr>
      <th
        v-for="header in props.headers"
        :key="header.value"
        role="columnheader"
        scope="col"
        :class="[
          'text-start',
          header.sortable !== false ? 'sortable' : null,
          ...sortable(props, header),
        ]"
        @click.prevent="toggleSort(header)"
        :style="
          header.width
            ? `width: ${header.width}; min-width: ${header.width};`
            : null
        "
      >
        <span>{{ header.text }}</span>
        <v-icon
          v-if="header.sortable !== false"
          class="v-data-table-header__icon"
          style="font-size: 18px"
        >
          mdi-arrow-up
        </v-icon>
      </th>
    </tr>
  </thead>
</template>

<script>
export default {
  name: "ClientSortDataTable",
  props: {
    props: {
      type: Object,
      required: true,
    },
    sortBy: {
      type: Array,
    },
    sortDesc: {
      type: Array,
    },
  },
  methods: {
    sortable(props, header) {
      const sortIndex = this.sortBy.findIndex((k) => k === header.value);
      const beingSorted = sortIndex >= 0;
      const isDesc = this.sortDesc[sortIndex];
      if (beingSorted) {
        return ["active", isDesc ? "desc" : "asc"];
      }
      return [];
    },
    toggleSort(header) {
      if (header.sortable === false) {
        return;
      }
      const isSameColumn = header.value === this.sortBy[0];

      this.$emit("update:sortBy", [header.value]);
      this.$emit("update:sortDesc", [isSameColumn ? !this.sortDesc[0] : false]);
    },
  },
};
</script>

<style scoped></style>
