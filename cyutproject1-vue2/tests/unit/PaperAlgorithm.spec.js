import items from "./data/items.json";
import MasterExamApi from "@/services/MasterExamApi";
import collect from "collect.js";

describe("Paper", () => {
  it("masteryExamAlgS2 method test", () => {
    // page 55, Item (4)
    const result = MasterExamApi.masteryExamAlgS2(items, 0.7, 0.8, 0.9, 3);
    expect(result).toStrictEqual([2.372, 2.831, 3.246]);
  });

  it("sort near info theta test", () => {
    const result = MasterExamApi.sortNearInfoTheta(items, 2.831);
    // page 54, Table 7
    // 題目順序: 1, 3, 2
    expect(result[0].maxInfoTheta).toBe(items[0].maxInfoTheta);
    expect(result[1].maxInfoTheta).toBe(items[2].maxInfoTheta);
    expect(result[2].maxInfoTheta).toBe(items[1].maxInfoTheta);
    expect(collect(result).pluck("dist").toArray()).toStrictEqual([
      0.182, 1.324, 2.583,
    ]);
  });

  it("algorithm test", function () {
    // page 57, Table 9
    const result = MasterExamApi.masteryExamAlg(
      items,
      0.7,
      0.8,
      0.9,
      3,
      0.3,
      1
    );
    expect(result.items.length).toBe(1);
    expect(result.items[0].items.length).toBe(1);
    expect(result.bestRate).toBe(0.29);
  });

  it("get Max. misclassification probability in items", function () {
    const { cutoffNumber, maxMisclassificationProbValue } =
      MasterExamApi.getMaxMisclassificationProbabilityValue(
        items,
        items,
        0.7,
        0.8,
        0.9,
        3
      );
    expect(cutoffNumber).toBe(3);
    expect(maxMisclassificationProbValue).toBe(0.343);
  });
});
