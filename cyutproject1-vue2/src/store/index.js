import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    isPrint: false,
    drawer: false,
    currentCourse: null,
  },
  mutations: {
    setIsPrint(state, boolean) {
      state.isPrint = boolean;
    },
    setDrawer(state, value) {
      state.drawer = value;
    },
    setCurrentCourse(state, value) {
      state.currentCourse = value;
    },
  },
  actions: {},
  modules: {},
});
