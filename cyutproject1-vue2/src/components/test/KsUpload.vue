<template>
  <v-container>
    <h2>專家知識結構匯入</h2>
    <v-col>
      <v-file-input
        accept=".json"
        label="Ks Upload"
        show-size
        small-chips
        v-model="file"
        multiple
        color="#7199de"
        @change="Preview_file"
      >
        <template v-slot:selection="{ text }">
          <v-chip small label color="#1867C0">
            {{ text }}
          </v-chip>
        </template>
      </v-file-input>
    </v-col>
    <v-col>
      <div v-for="(url, index) in url" :key="index">
        {{ file[index].name }}<br />
        {{ ks[index] }}
        <hr />
      </div>
    </v-col>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "KsUpload",
  data() {
    return {
      file: null,
      url: [],
      ks: [],
    };
  },
  methods: {
    async Preview_file() {
      this.url = [];
      this.ks = [];
      for (let i = 0; i < this.file.length; i++) {
        this.url.push(URL.createObjectURL(this.file[i]));
        let response = await axios.get(this.url[i]).then((response) => {
          return response.data;
        });
        this.ks.push(response);
      }
    },
  },
};
</script>
