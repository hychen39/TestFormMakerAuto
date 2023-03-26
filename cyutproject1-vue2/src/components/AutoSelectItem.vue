<template>
  <div>
    <v-container v-if="state == 'start'" style="text-align: center">
      <v-col md="4" offset="4">
        <h1 v-text="'KSAT測驗'"></h1>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          elevation="2"
          @click="testStart"
          style="right: 10px"
          :disabled="!isCorrect"
        >
          開始測驗
        </v-btn>
      </v-col>
      <v-col md="4" offset="4">
        題庫檔名:
        <v-text-field v-model="tableName"></v-text-field>
        專家知識結構檔名:
        <v-text-field v-model="ksName"></v-text-field>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          elevation="2"
          @click="importData"
          style="right: 10px"
        >
          匯入
        </v-btn>
      </v-col>
      <v-col md="4" offset="4" v-if="message != ''">
        <h3 style="color: red">
          {{ message }}
        </h3>
      </v-col>
    </v-container>
    <v-container v-if="state == 'testing'">
      <v-row>
        <v-col md="2">
          <v-btn color="primary" elevation="2" @click="testForCorrectAnswer">
            繼續測驗(答對)
          </v-btn>
        </v-col>
        <v-col>
          <v-btn color="primary" elevation="2" @click="testForWrongAnswer">
            繼續測驗(答錯)
          </v-btn>
        </v-col>
      </v-row>
      <v-col>
        <vue-markdown :source="testItem.question"></vue-markdown>
      </v-col>
    </v-container>
    <v-container v-if="state == 'end' || state == 'diagnosis'">
      <v-col>
        <h2 v-text="'診斷結果'" style="text-align: center"></h2>
      </v-col>
      <v-col v-if="state == 'end'" style="text-align: center">
        <v-btn color="primary" elevation="2" @click="testDiagnosis">
          顯示診斷結果
        </v-btn>
      </v-col>
      <v-col v-if="state == 'diagnosis'">
        <v-card>
          <v-simple-table>
            <template v-slot:default>
              <thead>
                <tr class="headers">
                  <th class="text-left">Topic id</th>
                  <th class="text-left">Topic name</th>
                  <th class="text-left">is Mastery?</th>
                  <th class="text-left">Possible issue</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(topic, index) in results"
                  :key="index"
                  :class="index % 2 === 0 ? 'style-1' : 'style-2'"
                >
                  <td>{{ topic.topicId }}</td>
                  <td>{{ topic.topicName }}</td>
                  <td>{{ "X" }}</td>
                  <td>
                    {{
                      "[" +
                      topic.topicIssue +
                      "]" +
                      "(" +
                      topic.topicCorrect +
                      ")"
                    }}
                  </td>
                </tr>
              </tbody>
            </template>
          </v-simple-table>
        </v-card>
      </v-col>
      <v-col v-if="state == 'diagnosis' && results.length == 0">
        <div v-text="'此單元概念皆已精熟'"></div>
      </v-col>
    </v-container>
    <v-container v-if="state == 'testing' || state == 'diagnosis'">
      <v-col>
        測試資料
        <hr />
        選中題目ID:
        <template v-for="item in testArray">
          <li :key="item.topicId">
            {{ "topic ID: " + item.topicId + " item ID: " + item.itemId }}
          </li>
        </template>
        <v-spacer></v-spacer>
        Finish Array:
        <template v-for="item in finishArray">
          <li :key="item.topicId">
            {{
              "topic ID: " +
              item.topicId +
              " item ID: " +
              item.itemId +
              " top: " +
              item.top +
              " correct topic ID: " +
              item.correctTopicId
            }}
          </li>
        </template>
      </v-col>
    </v-container>
  </div>
</template>

<script>
import TopicApi from "@/services/old/TopicApi";
import ItemApi from "@/services/old/ItemApi";
import VueMarkdown from "@adapttive/vue-markdown";

export default {
  name: "AutoSelectItem",
  data() {
    return {
      ItemId_TopicId: null,
      ks: null,
      answer: null,
      testArray: [],
      finishArray: [],
      topicTestedArray: [],
      istest: false,
      state: "start",
      testItem: "",
      results: [],
      tableName: "ItemId_TopicId.json",
      ksName: "knowledgeStructure_test.json",
      isCorrect: false,
      alert: false,
      message: "",
    };
  },
  components: {
    VueMarkdown,
  },
  methods: {
    // KSAT測驗開始
    testStart() {
      // 1. 挑選出沒有父節點的知識節點。
      let topTopics = [];
      this.ks.forEach((topic) => {
        if (topic.parentId == null) {
          topic.top = null;
          topTopics.push(topic);
        }
      });
      console.log(topTopics);
      // 2. 進行隨機選題
      this.testArray = this.testReturnRandomItems(topTopics);
      // 3. 更改考試狀態
      this.istest = true;
      this.state = "testing";
      this.getTestItem();
      // 答案測試
      console.log(this.testArray);
    },
    // 驗證答案並分類
    async testVerifyAnwser() {
      // 已測驗概念加入 topicTestedArray
      this.topicTestedArray.push(
        this.testArray[this.testArray.length - 1].topicId
      );
      // 如果正在考試
      if (this.istest) {
        // 取得試題答案
        let answer = await ItemApi.getAnswer(
          this.testArray[this.testArray.length - 1].itemId
        );
        // 如果答對
        if (this.testArray[this.testArray.length - 1].answer == answer) {
          // finishArray 添加一筆資料，用於診斷方法。
          this.finishArray.push({
            topicId: this.testArray[this.testArray.length - 1].topicId,
            itemId: this.testArray[this.testArray.length - 1].itemId,
            top: this.testArray[this.testArray.length - 1].top,
            correctTopicId: this.testArray[this.testArray.length - 1].topicId,
          });
          // 從 testArray 移除這次測驗試題。
          this.testArray.pop();
          // 如果答錯
        } else {
          // 儲存這次測驗試題資料。
          let temp = this.testArray[this.testArray.length - 1];
          // 是否有子節點
          let haveSub = false;
          // 從 testArray 移除這次測驗試題。
          this.testArray.pop();
          // 尋找包含此次概念 ID 的子節點
          this.ks.forEach((topic) => {
            // 如果此節點有父節點 && 如果此節點沒有被測驗過 && 如果此節點包含父節點
            if (
              topic.parentId != null &&
              !this.topicTestedArray.includes(topic.id) &&
              topic.parentId.includes(temp.topicId)
            ) {
              // testArray 添加子節點資料
              if (temp.top == null) {
                let top = [];
                top.push(temp.topicId);
                this.testArray.push({
                  topicId: topic.id,
                  itemId: this.testReturnRandomItem(topic.id),
                  top: top,
                  answer: null,
                });
                haveSub = true;
              } else {
                temp.top.push(temp.topicId);
                this.testArray.push({
                  topicId: topic.id,
                  itemId: this.testReturnRandomItem(topic.id),
                  top: temp.top,
                  answer: null,
                });
                haveSub = true;
              }
            }
          });
          // 如果沒有子節點
          if (!haveSub) {
            if (temp.top == null) {
              let top = [];
              top.push(temp.topicId);
              // finishArray 添加此次試題資料
              this.finishArray.push({
                topicId: temp.topicId,
                itemId: temp.itemId,
                top: top,
                correctTopicId: "X",
              });
            } else {
              temp.top.push(temp.topicId);
              // finishArray 添加此次試題資料
              this.finishArray.push({
                topicId: temp.topicId,
                itemId: temp.itemId,
                top: temp.top,
                correctTopicId: "X",
              });
            }
          }
        }
        // 判斷 testArray 是否為空陣列
        this.testIfTestArrayEmpty();
        // 取得下一個試題資料
        this.getTestItem();
      }
    },
    testIfTestArrayEmpty() {
      // 確認答錯陣列是否為零
      if (this.testArray.length == 0) {
        this.istest = false;
        this.state = "end";
        console.log("testArray.length == 0");
      }
    },
    // input: topicList, Output: itemList
    testReturnRandomItems(topicList) {
      let testItemList = [];
      // 1. 過濾出相同標籤ID的試題ID。
      topicList.forEach((selectedTopic) => {
        let itemList = this.ItemId_TopicId.filter(
          (topic) => topic.topicId == selectedTopic.id
        );
        // 2. 在過濾出來的試題ID陣列隨機挑選出一題。
        testItemList.push({
          topicId: selectedTopic.id,
          itemId: itemList[Math.floor(Math.random() * itemList.length)].itemId,
          top: selectedTopic.top,
          answer: null,
        });
      });
      return testItemList;
    },
    // input: topicId, Output: itemId
    testReturnRandomItem(topicId) {
      let itemList = this.ItemId_TopicId.filter(
        (topic) => topic.topicId == topicId
      );
      return itemList[Math.floor(Math.random() * itemList.length)].itemId;
    },
    // 給予正確答案並驗證
    async testForCorrectAnswer() {
      if (this.istest) {
        // 1. 給予目前測驗的題目答案
        let answer = await ItemApi.getAnswer(
          this.testArray[this.testArray.length - 1].itemId
        );
        this.testArray[this.testArray.length - 1].answer = answer;
        this.testVerifyAnwser();
      }
    },
    // 給予錯誤答案並驗證
    testForWrongAnswer() {
      if (this.istest) {
        // 1. 給予目前測驗的題目答案
        let answer = "X";
        this.testArray[this.testArray.length - 1].answer = answer;
        this.testVerifyAnwser();
      }
    },
    // 提取試題題目
    async getTestItem() {
      // 判斷 testArray 是否有還有試題
      if (this.testArray.length != 0) {
        let item = await ItemApi.getItem(
          this.testArray[this.testArray.length - 1].itemId
        );
        this.testItem = item;
      }
    },
    // 列出診斷表格
    testDiagnosis() {
      this.state = "diagnosis";
      this.results = [];
      // let same = [];
      (async () => {
        for (let i = 0; i < this.finishArray.length; i++) {
          // 如果 top 是陣列
          if (Array.isArray(this.finishArray[i].top)) {
            let topicName = await TopicApi.getKsName(
              this.finishArray[i].top[this.finishArray[i].top.length - 1]
            );
            this.results.push({
              topicId:
                this.finishArray[i].top[this.finishArray[i].top.length - 1],
              topicName: topicName,
              topicIssue: this.finishArray[i].top,
              topicCorrect: this.finishArray[i].correctTopicId,
            });
            // same.push(
            //   this.finishArray[i].top[this.finishArray[i].top.length - 1]
            // );
          }
        }
        this.results.sort((a, b) => b.topicId - a.topicId);
      })();
    },
    async importData() {
      try {
        this.ks = await TopicApi.getKsByName(this.ksName);
        this.ItemId_TopicId = await TopicApi.getItemId_TopicId(this.tableName);
        this.message = "";
        this.isCorrect = true;
      } catch (e) {
        this.isCorrect = false;
        this.message = "ks or item DB not exist!";
      }
    },
  },
};
</script>

<style>
.headers {
  background-color: rgb(230, 230, 230);
}
.style-1 {
  background-color: rgb(255, 255, 255);
}
.style-2 {
  background-color: rgb(245, 245, 245);
}
</style>
