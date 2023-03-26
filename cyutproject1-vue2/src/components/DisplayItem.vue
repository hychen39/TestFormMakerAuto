<template>
  <div>
    <v-col cols="12" md="6">
      <v-text-field label="輸入題號" v-model="id" :rules="rules">
      </v-text-field>
      <v-btn color="primary" elevation="2" @click="importItem(id)">
        引進試題
      </v-btn>
      <v-btn color="primary" elevation="2" @click="changeState('all')">
        顯示全部
      </v-btn>
      <v-btn color="primary" elevation="2" @click="changeState('question')">
        顯示題目
      </v-btn>
      <v-btn color="primary" elevation="2" @click="changeState('answer')">
        顯示答案
      </v-btn>
      <v-btn color="primary" elevation="2" @click="changeState('title')">
        顯示標題
      </v-btn>
    </v-col>
    <v-col cols="12" md="8">
      <vue-markdown v-if="state == 'all'" :source="file"></vue-markdown>
      <vue-markdown
        v-if="state == 'question'"
        :source="question"
      ></vue-markdown>
      <vue-markdown v-if="state == 'answer'" :source="answer"></vue-markdown>
      <vue-markdown v-if="state == 'title'" :source="title"></vue-markdown>
    </v-col>
  </div>
</template>

<script>
import VueMarkdown from "@adapttive/vue-markdown";
import ItemApi from "@/services/old/ItemApi";

export default {
  name: "DisplayItem",
  data() {
    return {
      id: null,
      file: "",
      question: "",
      answer: "",
      title: "",
      state: "",
      rules: [
        (value) => !!value || "必填欄位",
        (value) => {
          const pattern = /^\d+$/;
          return pattern.test(value) || "格式錯誤";
        },
      ],
    };
  },
  components: {
    VueMarkdown,
  },
  methods: {
    async importItem(value) {
      let item;
      item = await ItemApi.getItem(value);
      this.file = item.all;
      this.title = item.title;
      this.question = item.question;
      this.answer = item.answer;
      this.state = "all";
    },
    changeState(state) {
      this.state = state;
    },
  },
};
</script>

<style>
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
</style>
