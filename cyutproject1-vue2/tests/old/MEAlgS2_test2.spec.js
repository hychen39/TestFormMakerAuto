/* eslint-disable */
import { round } from "mathjs";
import items from "../unit/data/items1.json";

describe("MEAtest", () => {
  // 此測試計算單一試題 Pi(theta)=0.7 的能力值
  it("MEAS2_test2", () => {
    // input: item(試題abc參數), target 目標分數(0.7), decimal 小數點後幾位(3)
    // output: 目標分數(0.7)的能力值
    function binarySearch(item, target, decimal) {
      // 轉換位數 -> 小數點3位轉成整數3位
      let tDecimal = 10 ** decimal;
      target = target * tDecimal;
      // 設置能力值範圍此為(-5~5)，轉換後為(-5000~5000)
      let [start, end] = [tDecimal * -5, tDecimal * 5];
      // 開始 binary search
      while (start <= end) {
        const mid = Math.floor((start + end) / 2);
        // 如果Pi(theta)等於目標分數(0.7)，轉換後為(700)，回傳中位數
        if (target === round(item.c + (1 - item.c) / (1 + Math.exp(-1.7 * item.a * (mid / tDecimal - item.b))), decimal) * tDecimal) {
          return mid;
        }
        // 如果Pi(theta)大於目標分數(0.7)，中位數-1當上界；如果Pi(theta)小於目標分數(0.7)，中位數+1當下界
        if (target < round(item.c + (1 - item.c) / (1 + Math.exp(-1.7 * item.a * (mid / tDecimal - item.b))), decimal) * tDecimal) {
          end = mid - 1;
        } else {
          start = mid + 1;
        }
      }
      // 查詢失敗回傳-1
      return -1;
    }
    console.log(binarySearch(items[3], 0.7, 3));
  });
});
