import axios from "axios";

axios.defaults.withCredentials = true;

export default {
  getItem(value) {
    return axios.get("items/Q" + value + ".md").then((response) => {
      let all, title, question, answer;
      all = response.data;
      all = all.replaceAll("img/", "items/img/");
      title = all.split("\n", 3)[2];
      question = all.split("### Ans")[0];
      answer = "### Ans" + all.split("### Ans")[1];
      return { all: all, title: title, question: question, answer: answer };
    });
  },
  getItems() {
    let items = [];
    (async () => {
      for (let i = 1; i <= 20; i++) {
        let response = await axios.get("items/Q" + i + ".md");
        response = response.data;
        let item = {
          id: null,
          all: "",
          question: "",
          answer: "",
          title: "",
        };
        item.id = response.split("\n", 1)[0];
        item.id = Number(item.id.split("Q", 2)[1]);
        item.all = response.replaceAll("img/", "items/img/");
        item.title = item.all.split("\n", 3)[2];
        item.title = "Q" + item.id + " " + item.title;
        item.question = item.all.split("### Ans")[0];
        item.answer = "### Ans" + item.all.split("### Ans")[1];
        items.push(item);
        items.sort((a, b) => a.id - b.id);
      }
    })();
    return items;
  },
  getAnswer(value) {
    return axios.get("items/Q" + value + ".md").then((response) => {
      let answer = response.data.split("Correct Answer: ")[1];
      answer = answer.split("\n")[0];
      answer = answer.trim();
      return answer;
    });
  },
  async getItemList() {
    let items = await axios.get(
      process.env.VUE_APP_SPRING_BOOT_REST_API + "items"
    );
    return items.data;
  },
  async getItemByQuestionNumber(questionNumber) {
    let item = await axios.get(
      process.env.VUE_APP_SPRING_BOOT_REST_API + "item/qn/" + questionNumber
    );
    return item.data;
  },
  addItem(item) {
    console.log(item);
    axios
      .post(process.env.VUE_APP_SPRING_BOOT_REST_API + "item", item)
      .then(function (response) {
        console.log(response);
      })
      .catch(function (error) {
        console.log(error);
      });
  },
  updateItemByQuestionNumber(item, questionNumber) {
    console.log(item);
    axios
      .put(
        process.env.VUE_APP_SPRING_BOOT_REST_API + "item/qn/" + questionNumber,
        item
      )
      .then(function (response) {
        console.log(response);
      })
      .catch(function (error) {
        console.log(error);
      });
  },
};
