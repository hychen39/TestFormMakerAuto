process.env.VUE_APP_VERSION = require("./package.json").version;

module.exports = {
  transpileDependencies: ["vuetify"],
  devServer: {
    port: 8080,
    proxy: {
      "/api": {
        target: process.env.VUE_APP_SPRINT_BOOT_PROXY_API,
        ws: true,
        changeOrigin: false,
      },
    },
  },
};
