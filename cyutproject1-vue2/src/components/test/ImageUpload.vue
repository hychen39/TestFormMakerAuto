<template>
  <div>
    <v-file-input
      accept="image/*"
      label="Image Upload"
      show-size
      small-chips
      v-model="image"
      multiple
      color="#7199de"
      @change="Preview_image"
    >
      <template v-slot:selection="{ text }">
        <v-chip small label color="#1867C0">
          {{ text }}
        </v-chip>
      </template>
    </v-file-input>
    <div v-for="(url, index) in url" :key="index">
      {{ image[index].name }}<br />
      <img :src="url" />
      <hr />
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      image: null,
      url: [],
    };
  },
  methods: {
    Preview_image() {
      this.url = [];
      this.image.forEach((img) => this.url.push(URL.createObjectURL(img)));
    },
  },
};
</script>
