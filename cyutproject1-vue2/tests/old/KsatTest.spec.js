import TopicApi from "@/services/old/TopicApi";

describe("KSAT", () => {
  it("ks survive?", () => {
    let topics = TopicApi.getKs();
    console.log(topics);
    let topTopics = [];
    let testItemList = [];
    topics.forEach((topic) => {
      if (topic.parentId == null) {
        topTopics.push(topic);
      }
    });
    console.log(topTopics);
    topTopics.forEach((selectedTopic) => {
      let itemList = TopicApi.getItemId_TopicId().filter(
        (topic) => topic.topicId == selectedTopic.id
      );
      testItemList.push(
        itemList[Math.floor(Math.random() * itemList.length)].itemId
      );
    });
    console.log(testItemList);
    console.log(Math.floor(testItemList[0] / 5));
    expect(Math.floor(testItemList[0] / 5)).toBe(1);
  });
});
