<template>
  <div>
    <v-col md="6">
      <v-card>
        <v-toolbar dark color="#555555">
          <v-toolbar-title style="margin: 0 0 10px">試題產生器</v-toolbar-title>
        </v-toolbar>
        <v-container>
          <v-row>
            <v-col md="5">
              選擇單元:
              <v-select
                v-model="topicSelectList"
                :items="topicList"
                item-text="name"
                label="選擇單元"
                solo
                return-object
                single-line
              ></v-select>
            </v-col>
            <v-col md="5" v-if="topicSelectList != null">
              選擇標籤:
              <v-select
                v-model="topicsSelectList"
                :items="topicSelectList.topics"
                item-text="name"
                label="選擇標籤"
                solo
                return-object
                single-line
              ></v-select>
            </v-col>
            <v-col md="2">
              <v-btn
                color="primary"
                elevation="2"
                :disabled="topicsSelectList == null"
                @click="addSelectedLabel"
                style="margin: 30px -5px"
              >
                新增
              </v-btn>
            </v-col>
          </v-row>
          <v-divider></v-divider>
          <v-row>
            <v-col md="3">
              <div v-text="'已挑選標籤'" style="margin: 20px" />
            </v-col>
            <v-col md="3">
              <div v-text="'總挑題數:'" style="margin: 20px" />
            </v-col>
            <v-col md="2">
              <v-text-field
                v-model="selectedCount"
                hide-details
                single-line
                type="number"
                min="0"
                suffix="題"
              ></v-text-field>
            </v-col>
            <v-col md="3" style="margin: 20px">
              {{ "目前: " + percentage + " %" }}
              <v-btn icon x-small @click="refreshPercentage">
                <v-icon>mdi-refresh</v-icon>
              </v-btn>
            </v-col>
          </v-row>
          <v-row>
            <template v-for="(item, i) in autoSelectedList">
              <v-list-item :key="i">
                <v-list-item-content style="padding: 0px">
                  <v-col md="8">
                    <v-list-item-title>
                      {{ i + 1 + ". " }}{{ item.name }}
                    </v-list-item-title>
                  </v-col>
                  <v-col md="2">
                    <v-list-item-title>
                      {{ "出題比例:" }}
                    </v-list-item-title>
                  </v-col>
                  <v-col md="2">
                    <v-text-field
                      v-model="item.value"
                      hide-details
                      single-line
                      type="number"
                      max="100"
                      min="0"
                      suffix="%"
                      style="margin: 0 0 10px"
                    ></v-text-field>
                  </v-col>
                </v-list-item-content>
              </v-list-item>
            </template>
          </v-row>
          <v-row>
            <v-col offset-md="8">
              <v-btn
                color="primary"
                elevation="2"
                :disabled="autoSelectedList.length == 0"
                @click="autoSelectedList = []"
              >
                清空
              </v-btn>
            </v-col>
            <v-col>
              <v-btn
                color="primary"
                elevation="2"
                :disabled="autoSelectedList.length == 0"
                @click="selectItemAction"
              >
                送出
              </v-btn>
            </v-col>
          </v-row>
        </v-container>
      </v-card>
    </v-col>
    <v-col>
      選中題目ID:
      <template v-for="item in testArray">
        <li :key="item.topicId">
          {{ "topic ID: " + item.topicId + " item ID: " + item.itemId }}
        </li>
      </template>
    </v-col>
    <v-snackbar v-model="snackbar" elevation="24">
      {{ message }}
    </v-snackbar>
    <v-btn color="primary" elevation="2" @click="testVerifyAnwser">
      繼續測驗
    </v-btn>
    <v-col>
      完成測驗ID:
      <template v-for="item in finishArray">
        <li :key="item.topicId">
          {{
            "topic ID: " +
            item.topicId +
            " item ID: " +
            item.itemId +
            " top: " +
            item.top
          }}
        </li>
      </template>
    </v-col>
  </div>
</template>

<script>
import TopicApi from "@/services/old/TopicApi";
import ItemApi from "@/services/old/ItemApi";

export default {
  name: "AutoSelectItem",
  data() {
    return {
      ItemId_TopicId: null,
      ks: null,
      topicList: [
        {
          id: 1,
          name: "第一單元",
          topics: [
            { id: 1, name: "自表格中選取欄位" },
            { id: 2, name: "欄位的運算" },
            { id: 3, name: "欄位的串接" },
            { id: 4, name: "欄位的命名" },
            { id: 5, name: "限制資料列" },
            { id: 6, name: "單一條件的運算子" },
            { id: 7, name: "結合多個單一條件的邏輯運算子" },
            { id: 8, name: "運算子" },
            { id: 9, name: "單元運算子" },
            { id: 10, name: "二元運算子" },
            { id: 11, name: "算術運算子" },
            { id: 12, name: "Concatenation Operator" },
            { id: 13, name: "比較運算子" },
            { id: 14, name: "區間比較" },
            { id: 15, name: "結合中的元素判斷" },
            { id: 16, name: "樣式比對" },
            { id: 17, name: "NULL 值的判斷" },
            { id: 18, name: "運算子的優先順序" },
          ],
        },
      ],
      topicSelectList: null,
      topicsSelectList: null,
      isTopicSelected: false,
      autoSelectedList: [],
      snackbar: false,
      message: "",
      selectedCount: 0,
      percentage: 0,
      selectedItemId: [],
      answer: null,
      testArray: [],
      finishArray: [],
      istest: false,
    };
  },
  methods: {
    addSelectedLabel() {
      var self = this;
      var isExist = false;
      this.autoSelectedList.forEach(function (topic) {
        if (self.topicsSelectList.id == topic.id) {
          isExist = true;
        }
      });
      if (!isExist) {
        this.topicsSelectList.value = 0;
        this.autoSelectedList.push(this.topicsSelectList);
      } else if (isExist) {
        this.message = "此標籤已存在!";
        this.snackbar = true;
        setTimeout(function () {
          self.snackbar = false;
        }, 2000);
      }
    },
    refreshPercentage() {
      var self = this;
      this.percentage = 0;
      this.autoSelectedList.forEach(function (topic) {
        self.percentage += parseInt(topic.value);
      });
    },
    selectItemAction() {
      var self = this;
      var itemList = [];
      this.selectedItemId = [];
      this.autoSelectedList.forEach(function (selectedTopic) {
        itemList = self.ItemId_TopicId.filter(
          (topic) => topic.topicId == selectedTopic.id
        );
        self.selectedItemId.push(
          itemList[Math.floor(Math.random() * itemList.length)].itemId
        );
      });
    },
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
      // 答案測試
      console.log(this.selectedItemId);
    },
    async testVerifyAnwser() {
      this.testForAnswer();
      let answer = await ItemApi.getAnswer(
        this.testArray[this.testArray.length - 1].itemId
      );
      if (this.testArray[this.testArray.length - 1].answer == answer) {
        this.finishArray.push({
          topicId: this.testArray[this.testArray.length - 1].topicId,
          itemId: this.testArray[this.testArray.length - 1].itemId,
          top: this.testArray[this.testArray.length - 1].top,
        });
        this.testArray.pop();
      } else {
        let temp = this.testArray[this.testArray.length - 1];
        let haveSub = false;
        this.testArray.pop();
        this.ks.forEach((topic) => {
          if (topic.parentId != null) {
            if (topic.parentId.includes(temp.topicId)) {
              this.testArray.push({
                topicId: topic.id,
                itemId: this.testReturnRandomItem(topic.id),
                top: temp.topicId,
                answer: null,
              });
              haveSub = true;
            }
          }
        });
        if (!haveSub) {
          this.finishArray.push({
            topicId: temp.topicId,
            itemId: temp.itemId,
            top: temp.topicId,
          });
        }
      }
    },
    testIfTestArrayEmpty() {
      // 確認答錯陣列是否為零
      if (this.testArray.length == 0) {
        this.istest = false;
        console.log("testArray.length == 0");
      }
    },
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
    testReturnRandomItem(topicId) {
      let itemList = this.ItemId_TopicId.filter(
        (topic) => topic.topicId == topicId
      );
      return itemList[Math.floor(Math.random() * itemList.length)].itemId;
    },
    testForAnswer() {
      // 1. 給予目前測驗的題目答案
      this.testArray[this.testArray.length - 1].answer = "A";
    },
  },
  async mounted() {
    this.ks = await TopicApi.getKs();
    this.ItemId_TopicId = await TopicApi.getItemId_TopicId();
    this.testStart();
    this.answer = await ItemApi.getAnswer(3);
  },
};
</script>
