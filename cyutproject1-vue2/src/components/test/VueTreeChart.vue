<template>
  <v-container>
    <v-col>
      <vue-tree
        style="height: 500px; border: 1px solid gray"
        :dataset="treeData2"
        :config="treeConfig"
        linkStyle="straight"
        :collapse-enabled="true"
        v-if="displayPkn"
        :key="count"
      >
        <template v-slot:node="{ node, collapsed }">
          <div
            class="node"
            :style="{ border: collapsed ? '2px solid red' : '' }"
          >
            <div
              v-text="node.id + '. ' + node.name"
              style="padding: 4px 4px; font-weight: bold"
            ></div>
            <!-- <v-bottom-sheet>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  class="btn"
                  v-bind="attrs"
                  v-on="on"
                  color="green"
                  elevation="2"
                  icon
                  x-small
                >
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </template>
              <v-sheet height="200px">
                <v-container>
                  <v-col md="6" offset-md="3">
                    <h2>新增知識節點</h2>
                    <v-text-field
                      v-model="addNodeName"
                      label="新增子節點名稱"
                    ></v-text-field>
                    <v-btn
                      color="primary"
                      elevation="2"
                      @click="addNode(node.id)"
                    >
                      新增
                    </v-btn>
                  </v-col>
                </v-container>
              </v-sheet>
            </v-bottom-sheet> -->
          </div>
        </template>
      </vue-tree>
    </v-col>
    <v-col>
      <template v-if="!displayPkn">
        <h2 style="text-align: center">讀取中，請稍後！</h2>
        <v-text-field color="success" loading disabled></v-text-field>
      </template>
    </v-col>
  </v-container>
</template>

<script>
import VueTree from "@ssthouse/vue-tree-chart";

export default {
  name: "VueTreeChart",
  components: {
    VueTree,
  },
  data() {
    return {
      treeData: {
        id: 1,
        name: "conversion function",
        value: 800,
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
      treeConfig: { nodeWidth: 120, nodeHeight: 80, levelHeight: 120 },
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
      addNodeName: null,
      displayPkn: false,
      count: null,
    };
  },
  props: {
    treeDate3: Object,
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
      this.treeData2 = data[0];
    },
    addNode(id) {
      let obj = {
        id: this.count,
        name: this.addNodeName,
        parentId: id,
      };
      const node = this.searchNode(this.treeData2, id);
      if ("children" in node) {
        node.children.push(obj);
        this.addNodeName = null;
        this.count += 1;
      } else {
        node.children = [];
        node.children.push(obj);
        this.addNodeName = null;
        this.count += 1;
      }
    },
  },
  mounted() {
    var self = this;
    this.jsonToTree(this.jsonData);
    this.count = this.jsonData.length + 1;
    setTimeout(function () {
      self.displayPkn = true;
    }, 1000);
  },
};
</script>

<style scoped>
.node {
  width: 100px;
  padding: 6px;
  display: flex;
  justify-content: center;
  color: white;
  background-color: #4ea117;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}
.btn {
  margin: 5px 5px 5px 5px;
  background-color: white;
}
</style>
