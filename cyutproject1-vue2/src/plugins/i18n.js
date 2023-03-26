import Vue from "vue";
import VueI18n from "vue-i18n";
import vuetifyLangZhHant from "vuetify/lib/locale/zh-Hant";
import vuetifyENLange from "vuetify/lib/locale/en";
import validateEN from "vee-validate/dist/locale/en.json";
import validateTW from "vee-validate/dist/locale/zh_TW.json";
import en from "@/locales/en.json";
import zhHant from "@/locales/zhHant.json";

Vue.use(VueI18n);

const messages = {
  en: {
    $vuetify: vuetifyENLange,
    validation: validateEN.messages,
    ...en,
  },
  zhHant: {
    $vuetify: vuetifyLangZhHant,
    validation: validateTW.messages,
    ...zhHant,
  },
};

export default new VueI18n({
  locale: "en",
  messages,
});
