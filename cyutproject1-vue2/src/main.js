import Vue from "vue";
import AsyncComputed from "vue-async-computed";
import App from "@/App.vue";
import router from "@/router";
import store from "@/store";
import vuetify from "@/plugins/vuetify";
import i18n from "@/plugins/i18n";
import "@/plugins/primeVue";
import VMdEditor from "@kangc/v-md-editor/lib/codemirror-editor";
import githubTheme from "@kangc/v-md-editor/lib/theme/github.js";
import enUS from "@kangc/v-md-editor/lib/lang/en-US";
import vuejsStorage from "vuejs-storage";
import { UrlMapperPlugin } from "url-mapper-vue";
import hljs from "highlight.js";
import Codemirror from "codemirror";
import "@kangc/v-md-editor/lib/style/codemirror-editor.css";
import "@kangc/v-md-editor/lib/theme/style/github.css";
import "@/plugins/validation";
// mode
import "codemirror/mode/markdown/markdown";
import "codemirror/mode/javascript/javascript";
import "codemirror/mode/css/css";
import "codemirror/mode/htmlmixed/htmlmixed";
import "codemirror/mode/vue/vue";
// edit
import "codemirror/addon/edit/closebrackets";
import "codemirror/addon/edit/closetag";
import "codemirror/addon/edit/matchbrackets";
// placeholder
import "codemirror/addon/display/placeholder";
// active-line
import "codemirror/addon/selection/active-line";
// scrollbar
import "codemirror/addon/scroll/simplescrollbars";
import "codemirror/addon/scroll/simplescrollbars.css";
// style
import "codemirror/lib/codemirror.css";

Vue.use(AsyncComputed);
Vue.use(vuejsStorage);
Vue.use(UrlMapperPlugin);

Vue.config.productionTip = false;

VMdEditor.lang.use("en-US", enUS);
VMdEditor.Codemirror = Codemirror;
VMdEditor.use(githubTheme, {
  Hljs: hljs,
});

Vue.use(VMdEditor);

new Vue({
  router,
  store,
  i18n,
  vuetify: vuetify(i18n),
  render: (h) => h(App),
}).$mount("#app");
