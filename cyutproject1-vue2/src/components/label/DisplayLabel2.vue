<template>
  <div>
    <v-container fluid>
      <v-row>
        <v-col md="12">
          <template v-for="label in labels">
            <v-chip
              id="chip1"
              outlined
              label
              :key="label.id"
              @click="putLabel(label)"
              color="blue darken-3"
            >
              <div v-text="label.id + '.' + label.content"></div>
            </v-chip>
          </template>
        </v-col>
      </v-row>
    </v-container>
    <v-container fluid v-if="isManaged == true">
      <v-btn
        color="primary"
        elevation="2"
        @click="returnItemsList()"
        style="left: 1em"
      >
        返回試題列表
      </v-btn>
      <CreateLabel @newLabel="importLabel"></CreateLabel>
      <DeleteLabel @delLabel="removeLabel"></DeleteLabel>
    </v-container>
  </div>
</template>

<script>
import CreateLabel from "@/components/label/CreateLabel";
import DeleteLabel from "@/components/label/DeleteLabel";

export default {
  name: "DisplayLabel2",
  data() {
    return {
      createState: false,
      deleteState: false,
      isActive: false,
      labels: [
        { id: 1, content: "conversion function" },
        { id: 2, content: "date format model" },
        { id: 3, content: "multiple-row operator" },
        { id: 4, content: "outer join" },
        { id: 5, content: "self join" },
        { id: 6, content: "correlated subquery" },
        { id: 7, content: "exist" },
        { id: 8, content: "NOT IN operator" },
      ],
    };
  },
  components: {
    CreateLabel,
    DeleteLabel,
  },
  methods: {
    importLabel(newLabel) {
      var self = this;
      var isExist = false;
      let tempLabel = { id: null, content: null };
      this.labels.forEach(function (label) {
        if (label.id == newLabel.id) {
          isExist = true;
        }
      });
      if (!isExist) {
        tempLabel.id = Number(newLabel.id);
        tempLabel.content = newLabel.content;
        this.labels.push(tempLabel);
      } else if (isExist) {
        this.$emit("existLabel");
      }
      this.labels.sort((a, b) => a.id - b.id);
    },
    removeLabel(deletedLabeId) {
      var isExist = false;
      this.labels.forEach(function (label) {
        if (label.id == deletedLabeId) {
          isExist = true;
        }
      });
      if (isExist) {
        this.labels = this.labels.filter((label) => label.id != deletedLabeId);
      } else if (!isExist) {
        this.$emit("notExistLabel");
      }
    },
    putLabel(label) {
      this.$emit("putLabel", label);
    },
    returnItemsList() {
      this.$emit("returnList");
    },
  },
  props: {
    isManaged: null,
  },
};
</script>

<style>
#chip1 {
  margin: 1.5px 1.5px 1.5px 1.5px;
}
.normalHeight {
  max-height: 130px;
  overflow: auto;
}
</style>
