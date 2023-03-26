import Vue from "vue";
import Vuetify from "vuetify/lib/framework";

Vue.use(Vuetify);

export default (i18n) =>
  new Vuetify({
    lang: {
      t: (key, ...params) => i18n.t(key, params),
    },
  });
