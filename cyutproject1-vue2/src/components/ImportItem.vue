<template>
  <div>
    <v-col cols="12" md="6">
      <v-textarea
        solo
        v-model="importText"
        id="textarea1"
        label="Json 格式轉換"
      ></v-textarea>
      <v-btn color="primary" elevation="2" @click="transferItem">
        Json轉換
      </v-btn>
      <p v-if="obj" id="message">格式錯誤</p>
      <br />
      <div v-for="(item, index) in item" :key="index">
        <div v-html="item.question"></div>
        <p v-text="'A. ' + item.A" />
        <p v-text="'B. ' + item.B" />
        <p v-text="'C. ' + item.C" />
        <p v-text="'D. ' + item.D" />
        <p v-text="'E. ' + item.E" v-if="item.E != null" />
      </div>
    </v-col>
  </div>
</template>

<script>
export default {
  name: "ImportItem",
  data() {
    return {
      importText: null,
      item: null,
      obj: false,
    };
  },
  methods: {
    transferItem() {
      try {
        this.item = JSON.parse(this.importText.replace(/\n/g, ""));
        this.obj = false;
      } catch (exception) {
        this.obj = true;
      }
    },
  },
};
</script>

<style>
#message {
  color: red;
  font-size: 30px;
}
</style>
