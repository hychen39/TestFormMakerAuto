<template>
  <v-container style="width: 800px">
    <v-row>
      <v-col md="12">
        <v-treeview
          v-model="selection"
          selectable
          hoverable
          :items="treeData2"
          return-object
          v-if="treeState"
          :key="count"
        >
          <template v-slot:label="{ item }">
            {{ item.name }}
            <v-btn icon x-small @click="addChild(item)">
              <v-icon>mdi-plus</v-icon>
            </v-btn>
            <v-dialog width="500">
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  x-small
                  v-bind="attrs"
                  v-on="on"
                  @click="editNameBtn(item)"
                >
                  <v-icon>mdi-circle-edit-outline</v-icon>
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5 grey lighten-2">
                  重新命名
                </v-card-title>
                <v-col>
                  <v-text-field
                    v-model="editName"
                    @keypress.enter="editNameAction(item)"
                  ></v-text-field>
                </v-col>
                <v-divider></v-divider>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="primary" text @click="editNameAction(item)">
                    確認
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </template>
        </v-treeview>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: "VuetifyTreeView",
  components: {},
  data() {
    return {
      treeData: [
        {
          id: 1,
          name: "conversion function",
          state: {
            expanded: true,
          },
          children: [
            {
              id: 2,
              name: "date format model",
              value: 400,
              children: [
                {
                  id: 3,
                  name: "multiple-row operator",
                  value: 10000,
                },
                {
                  id: 4,
                  name: "outer join",
                  value: 30000,
                },
                {
                  id: 5,
                  name: "self join",
                  value: 200,
                },
              ],
            },
            {
              id: 6,
              name: "correlated subquery",
              value: 200,
              children: [
                {
                  id: 7,
                  name: "exist",
                  value: 230,
                },
              ],
            },
            {
              id: 8,
              name: "NOT IN operator",
              value: 300,
            },
          ],
        },
      ],
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
      treeData2: null,
      treeState: false,
      selection: [],
      editName: "",
      count: 0,
    };
  },
  methods: {
    searchId(tree, target) {
      if (tree.id === target) {
        return tree.name;
      }

      if (tree.children != undefined) {
        for (const child of tree.children) {
          const res = this.searchId(child, target);

          if (res) {
            return res;
          }
        }
      }
    },
    searchName(tree, target) {
      if (tree.name === target) {
        return tree.id;
      }

      if (tree.children != undefined) {
        for (const child of tree.children) {
          const res = this.searchName(child, target);

          if (res) {
            return res;
          }
        }
      }
    },
    searchNode(tree, target) {
      if (tree.id === target) {
        return tree;
      }

      if (tree.children != undefined) {
        for (const child of tree.children) {
          const res = this.searchNode(child, target);

          if (res) {
            return res;
          }
        }
      }
    },
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
      this.treeData2 = data;
    },
    addChild(item) {
      console.log(item);
      if (!item.children) {
        item.children = [];
      }

      const name = "Default " + this.count;
      const id = this.count;
      item.children.push({
        id,
        name,
      });
      this.count += 1;
    },
    editNameBtn(item) {
      this.editName = item.name;
    },
    editNameAction(item) {
      console.log(item);
      item.name = this.editName;
      this.editName = "";
    },
  },
  mounted() {
    var self = this;
    this.jsonToTree(this.jsonData);
    this.count = this.jsonData.length + 1;
    setTimeout(function () {
      self.treeState = true;
    }, 500);
  },
};
</script>

<style></style>
