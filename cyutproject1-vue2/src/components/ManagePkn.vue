<template>
  <div>
    <v-container>
      <v-row>
        <v-col md="4" style="overflow-x: scroll">
          <VuetifyTreeView></VuetifyTreeView>
        </v-col>
        <v-col md="8">
          <VueTreeChart></VueTreeChart>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import VueTreeChart from "@/components/test/VueTreeChart";
import VuetifyTreeView from "@/components/test/VuetifyTreeView";

export default {
  name: "ManagePkn",
  components: {
    VueTreeChart,
    VuetifyTreeView,
  },
  date() {
    return {};
  },
};
</script>
