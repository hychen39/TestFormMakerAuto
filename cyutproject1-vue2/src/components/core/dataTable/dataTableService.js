import queryString from "query-string";

export default {
  data() {
    return {
      sortBy: [],
      sortDesc: [],
      dataTablePagination: {
        page: 1,
        itemsPerPage: 10,
      },
      dataTableQueries: null,
    };
  },
  computed: {
    currentPageReportTemplate() {
      return `${this.$t("pagination.Showing")} {first} ${this.$t(
        "pagination.To"
      )} {last} ${this.$t("pagination.Of")} {totalRecords} ${this.$t(
        "pagination.Records"
      )} `;
    },
  },
  methods: {
    onPage(event) {
      this.dataTablePagination.page = event.page + 1;
    },
    onSort(event) {
      if (event.sortField) {
        this.sortBy = [event.sortField];
        this.sortDesc = [event.sortOrder === 1];
      } else {
        this.sortBy = [];
        this.sortDesc = [];
      }
    },
    initialPages() {
      const config = {
        page: { path: "dataTablePagination.page", type: "number" },
        itemsPerPage: {
          path: "dataTablePagination.itemsPerPage",
          type: "number",
        },
      };
      this.$setDataFromUrl(config);
      const { page, itemsPerPage } = this.dataTablePagination;

      // workaround for initial page
      this.$refs.dataTable.$children
        .filter((vm) => vm.$vnode.tag.includes("DTPaginator"))
        .forEach((paginator) => {
          paginator.$data.d_first = page * itemsPerPage - 1;
        });
    },
    setPaginationToUrl(options = {}) {
      this.$setUrlParams({
        ...options,
        page: (this.dataTablePagination?.page ?? 1).toString(),
        itemsPerPage: (this.dataTablePagination?.itemsPerPage ?? 10).toString(),
      });
      this.dataTableQueries = queryString.parse(location.search);
    },
  },
};
