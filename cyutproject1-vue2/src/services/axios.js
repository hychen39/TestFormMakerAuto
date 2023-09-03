import axios from "axios";

axios.defaults.withCredentials = true;
axios.defaults.baseURL = window.API_URL;

export default axios;
