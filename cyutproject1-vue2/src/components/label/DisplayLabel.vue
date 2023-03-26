<template>
  <div>
    <v-container style="width: 500px">
      <v-row>
        <v-col md="12">
          <v-treeview
            v-model="selection"
            selectable
            hoverable
            :items="treeData"
            return-object
            v-if="treeState"
            @input="selectLabel"
          >
          </v-treeview>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
export default {
  name: "DisplayLabel",
  data() {
    return {
      jsonData: [
        {
          id: 1,
          name: "conversion function",
          parentId: null,
        },
        {
          id: 2,
          name: "date format model",
          parentId: 1,
        },
        {
          id: 3,
          name: "multiple-row operator",
          parentId: 2,
        },
        {
          id: 4,
          name: "outer join",
          parentId: 2,
        },
        {
          id: 5,
          name: "self join",
          parentId: 2,
        },
        {
          id: 6,
          name: "correlated subquery",
          parentId: 1,
        },
        {
          id: 7,
          name: "exist",
          parentId: 6,
        },
        {
          id: 8,
          name: "NOT IN operator",
          parentId: 1,
        },
      ],
      treeData: null,
      treeState: false,
      selection: [],
    };
  },
  props: {
    change: Boolean,
  },
  methods: {
    jsonToTree(arr) {
      const data = arr.reduce(function (r, a) {
        function getParent(s, b) {
          return b.id === a.parentId
            ? b
            : b.children && b.children.reduce(getParent, s);
        }
        var index = 0,
          node;
        if ("parentId" in a) {
          node = r.reduce(getParent, {});
        }
        if (node && Object.keys(node).length) {
          node.children = node.children || [];
          node.children.push(a);
        } else {
          while (index < r.length) {
            if (r[index].parentId === a.id) {
              a.children = (a.children || []).concat(r.splice(index, 1));
            } else {
              index++;
            }
          }
          r.push(a);
        }
        return r;
      }, []);
      this.treeData = data;
    },
    selectLabel(item) {
      this.$emit("addLabel", item);
    },
  },
  watch: {
    change() {
      this.selection = [];
    },
  },
  mounted() {
    var self = this;
    this.jsonToTree(this.jsonData);
    setTimeout(function () {
      self.treeState = true;
    }, 500);
  },
};
</script>

<style></style>
