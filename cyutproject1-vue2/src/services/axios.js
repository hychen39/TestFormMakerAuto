import axios from "axios";

const CancelToken = axios.CancelToken;
const requestPool = {};

axios.defaults.withCredentials = true;
axios.defaults.baseURL = process.env.VUE_APP_SPRING_BOOT_REST_API;

axios.interceptors.request.use(
  (config) => {
    if (requestPool[config.url]) {
      const cancel = requestPool[config.url];
      delete requestPool[config.url];
      cancel(
        JSON.stringify({
          status: "cancel",
          message: "Cancel repeated request:" + config.url,
        })
      );
    }

    config.cancelToken = new CancelToken(function executor(c) {
      requestPool[config.url] = c;
    });

    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

export default axios;
