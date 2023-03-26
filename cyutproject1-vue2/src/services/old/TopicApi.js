import axios from "axios";

axios.defaults.withCredentials = true;

export default {
  getItemId_TopicId() {
    return axios.get("data/ItemId_TopicId.json").then((response) => {
      return response.data;
    });
  },
  getKs() {
    return axios.get("data/knowledgeStructure_test.json").then((response) => {
      return response.data;
    });
  },
  getKsName(id) {
    return axios.get("data/knowledgeStructure_test.json").then((response) => {
      return response.data[id - 1].name;
    });
  },
  getKsByName(ksName) {
    return axios.get("data/" + ksName).then((response) => {
      return response.data;
    });
  },
  getItemId_TopicIdTable(tableName) {
    return axios.get("data/" + tableName).then((response) => {
      return response.data;
    });
  },
};
