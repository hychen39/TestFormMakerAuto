<template>
  <v-container fluid>
    <v-alert v-model="alert" border="bottom" color="red" text type="error">
      {{ message }}
    </v-alert>
    <v-card>
      <v-toolbar dark color="#555555">
        <v-row>
          <v-col md="3">
            <v-toolbar-title style="margin: 4px">標籤列表</v-toolbar-title>
          </v-col>
          <v-col>
            <v-toolbar-title style="margin: 4px">試題列表</v-toolbar-title>
          </v-col>
          <v-col md="3">
            <v-text-field
              v-model="search"
              append-icon="mdi-magnify"
              label="Search "
              single-line
            ></v-text-field>
          </v-col>
        </v-row>
      </v-toolbar>
      <v-row no-gutters>
        <v-col
          style="overflow-x: scroll; overflow-y: scroll; max-height: 460px"
        >
          <DisplayLabel
            @addLabel="addLabeltoItem"
            :change="isChanged"
          ></DisplayLabel>
        </v-col>
        <v-col md="4" style="overflow-y: scroll; max-height: 460px">
          <v-radio-group
            v-model="selectedItem"
            :value="items[0]"
            @change="changeItem"
          >
            <template v-for="(item, i) in searchItems">
              <v-col md="12" :key="i">
                <v-radio :value="item">
                  <template v-slot:label>
                    <div id="item" v-text="item.title"></div>
                  </template>
                </v-radio>
                <v-row>
                  <template v-for="topic in item.topic">
                    <v-chip
                      id="label"
                      small
                      :key="topic.id"
                      close-icon="mdi-close"
                    >
                      {{ topic.id }}.{{ topic.name }}
                    </v-chip>
                  </template>
                </v-row>
              </v-col>
            </template>
          </v-radio-group>
        </v-col>
        <v-col
          md="5"
          style="overflow-y: scroll; max-height: 460px; padding: 10px 10px"
        >
          <vue-markdown
            v-if="selectedItem != null"
            :source="selectedItem.all"
          ></vue-markdown>
          <h2
            v-if="selectedItem == null"
            style="text-align: center; line-height: 400px"
          >
            預覽試題視窗
          </h2>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import VueMarkdown from "@adapttive/vue-markdown";
import DisplayLabel from "@/components/label/DisplayLabel";
import ItemApi from "@/services/old/ItemApi";

export default {
  name: "LabelItem2",
  data() {
    return {
      items: [],
      selectedItem: null,
      alert: false,
      message: null,
      search: null,
      isChanged: false,
    };
  },
  components: {
    VueMarkdown,
    DisplayLabel,
  },
  methods: {
    addLabeltoItem(label) {
      if (this.selectedItem == null) {
        this.message = "請選擇題目再選擇標籤！";
        this.alert = true;
        var self = this;
        setTimeout(function () {
          self.alert = false;
        }, 2000);
      } else if (this.selectedItem != null) {
        this.selectedItem.topic = label;
        this.items[this.selectedItem.id - 1].topic.sort((a, b) => a.id - b.id);
      }
    },
    changeItem(item) {
      this.selection = item.topic;
      this.isChanged = !this.isChanged;
    },
  },
  computed: {
    searchItems() {
      if (this.search == null) {
        return this.items;
      } else {
        return this.items.filter((item) => item.title.includes(this.search));
      }
    },
  },
  mounted() {
    this.items = ItemApi.getItems();
  },
};
</script>

<style>
#item {
  color: rgb(0, 0, 0);
}
#label {
  font-size: small;
  color: rgb(100, 100, 100);
  margin: 10px 0px 0px 5px;
  left: 0.5em;
}
img {
  max-width: 100%;
}
pre code {
  background: #f4f4f4;
  border: 1px solid #ddd;
  color: #666;
  page-break-inside: avoid;
  font-family: monospace;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 1.6em;
  max-width: 100%;
  overflow: auto;
  padding: 1em 1.5em;
  display: block;
  word-wrap: break-word;
}
.managedlabel {
  max-width: 500px;
}
ul {
  padding-left: 0px;
}
</style>
