/* eslint-disable */
import { round } from "mathjs";
import items from "../unit/data/items.json";

describe("MEAtest", () => {
  it("MEAS2_test1", () => {
    //二分搜尋法: 找出題庫平均 Pi(theta) = 0.7 的能力值
    // input: item(試題abc參數), target 門檻分數(0.7), decimal 小數點後幾位(3)
    // output: 門檻分數(0.7)的能力值
    function binarySearch(items, target, decimal) {
      // 轉換位數 -> 小數點3位轉成整數3位
      let tDecimal = 10 ** decimal;
      target = target * tDecimal;
      // 設置能力值範圍此為(-5~5)，轉換後為(-5000~5000)
      let [start, end] = [tDecimal * -5, tDecimal * 5];
      // 開始 binary search
      while (start <= end) {
        const mid = Math.floor((start + end) / 2);
        // 將能力值帶入所有試題算出平均Pi(theta)
        let averageCorrectRate = 0;
        for (let i = 0; i < items.length; i++) {
          averageCorrectRate +=
          round(items[i].c + (1 - items[i].c) / (1 + Math.exp(-1.7 * items[i].a * (mid / tDecimal - items[i].b))), decimal) * tDecimal;
        }
        averageCorrectRate = round(averageCorrectRate / items.length);
        console.log(start, end, mid, averageCorrectRate);
        // 如果平均Pi(theta)等於目標分數(0.7)，轉換後為(700)，回傳中位數
        if (target === averageCorrectRate) {
          return mid / tDecimal;
        }
        // 如果平均Pi(theta)大於目標分數(0.7)，中位數-1當上界；如果Pi(theta)小於目標分數(0.7)，中位數+1當下界
        if (target < averageCorrectRate) {
          end = mid - 1;
        } else {
          start = mid + 1;
        }
      }
      // 查詢失敗回傳-1
      return -1;
    }
    // 精熟檢定演算法-第二階段
    // 說明: 1. (πl, π0, πu) -> (θl, θ0, θu) 2. 設定誤判率
    // input: 試題, 門檻下界(πl), 門檻分數(π0), 門檻上界(πu), 小數點位數, 誤判率
    // output: [轉換後的能力值下界(θl), 能力值(θ0), 能力值上界(θu), 設定誤判率(p*)]
    function meAS2(items, lower_target, target, upper_target, decimal, setP) {
      return [
        //binarySearch(items, lower_target, decimal),
        binarySearch(items, target, decimal),
        //binarySearch(items, upper_target, decimal),
        //setP
      ]
    }
    console.log(meAS2(items, 0.75, 0.8, 0.85, 3, 0.1));
  });
});
