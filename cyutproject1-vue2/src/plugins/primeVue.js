import Vue from "vue";
import PrimeVue from "primevue/config";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import "primevue/resources/primevue.min.css";
import "primevue/resources/themes/saga-blue/theme.css";
import "primeicons/primeicons.css";

Vue.use(PrimeVue);
Vue.component("DataTable", DataTable);
Vue.component("Column", Column);
