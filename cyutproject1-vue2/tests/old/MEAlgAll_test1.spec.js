import { round, ceil, combinations } from "mathjs";
import items from "../unit/data/items.json";

describe("MEAtest", () => {
  it("MEAlgaAll_test1", () => {
    /**
     * 二分搜尋法
     * 找出題庫平均 Pi(theta) = target 所需要的能力值
     * @param {*} items 題庫
     * @param {*} target 門檻分數 (πl or π0 or πu)
     * @param {*} decimal 計算小數點位數
     * @returns {number} 門檻分數為 target 的能力值 (θl or θ0 or θu)
     */
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
            round(
              items[i].c +
                (1 - items[i].c) /
                  (1 +
                    Math.exp(
                      -1.7 * items[i].a * (mid / tDecimal - items[i].b)
                    )),
              decimal
            ) * tDecimal;
        }
        averageCorrectRate = round(averageCorrectRate / items.length);
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
    /**
     * 精熟檢定演算法 3-1
     * 找出接近 theta0 的 item，根據距離排序題庫
     * @param {*} items 題庫
     * @param {*} theta0 能力值 (θ0)
     * @returns {Array} 排序後的題庫
     */
    function sortNearInfoTheta(items, theta0) {
      let nearMaxInfoThetaItemIndex = [];
      for (let i = 0; i < items.length; i++) {
        nearMaxInfoThetaItemIndex.push(items[i]);
        nearMaxInfoThetaItemIndex[i].dist = round(
          Math.abs(theta0 - items[i].maxInfoTheta),
          3
        );
      }
      return nearMaxInfoThetaItemIndex.sort((a, b) => a.dist - b.dist);
    }
    /**
     * 精熟檢定演算法 3-2
     * 將 (θl, θ0, θu) -> (π*l, π*0, π*u)，計算新題庫的門檻分數 π*
     * @param {*} newItems 經過距離排序後的題庫
     * @param {*} number 需計算的試題數量 (m)
     * @param {*} theta 能力值 (θl or θ0 or θu)
     * @returns {number} m 個試題下的新門檻分數 (π*l or π*0 or π*u)
     */
    function newThetaToNewPi(newItems, number, theta) {
      let averageCorrectRate = 0;
      for (let i = 0; i < number; i++) {
        averageCorrectRate += round(
          newItems[i].c +
            (1 - newItems[i].c) /
              (1 + Math.exp(-1.7 * newItems[i].a * (theta - newItems[i].b))),
          3
        );
      }
      return round(averageCorrectRate / number, 3);
    }
    /**
     * 精熟檢定演算法 3-3
     * 計算誤判率 Pm(n0)
     * @param {*} Pis 新門檻分數 [π*l, π*0, π*u]
     * @param {*} m 試題數
     * @returns {Array} [試題數 m, 門檻試題數 n0, 試卷誤判率 Pm(n0)]
     */
    function calcWrongRate(Pis, m) {
      let n0 = ceil(m * Pis[1], 0);
      let alpha = 0,
        beta = 0;
      // 計算 Pl(n0)=1-α(n0)
      for (let i = 0; i <= n0 - 1; i++) {
        alpha += combinations(m, i) * Pis[0] ** i * (1 - Pis[0]) ** (m - i);
      }
      alpha = round(1 - alpha, 3);
      // 計算 Pu(n0)=1-β(n0)
      for (let i = n0; i <= m; i++) {
        beta += combinations(m, i) * Pis[2] ** i * (1 - Pis[2]) ** (m - i);
      }
      beta = round(1 - beta, 3);
      console.log(
        "m = " +
          m +
          ", n0 = " +
          n0 +
          ", Pl(n0) = " +
          alpha +
          ", Pu(n0) = " +
          beta
      );
      return [m, n0, Math.max(alpha, beta)];
    }
    /**
     * 精熟檢定演算法-第二階段
     * 說明: 1. (πl, π0, πu) -> (θl, θ0, θu) 2. 設定誤判率
     * @param {*} items 題庫
     * @param {*} lower_target 門檻分數下界 (πl)
     * @param {*} target 門檻分數 (π0)
     * @param {*} upper_target 門檻分數上界 (πu)
     * @param {*} decimal 計算小數點位數
     * @param {*} setP 使用者設定誤判率 (p*)
     * @returns {Array} [轉換後的能力值下界(θl), 能力值(θ0), 能力值上界(θu), 設定誤判率(p*)]
     */
    function masteryExamAlgS2(
      items,
      lower_target,
      target,
      upper_target,
      decimal,
      setP
    ) {
      return [
        binarySearch(items, lower_target, decimal),
        binarySearch(items, target, decimal),
        binarySearch(items, upper_target, decimal),
        setP,
      ];
    }
    /**
     * 精熟檢定演算法-第三階段
     * @param {*} thetaL 能力值下界 (θl)
     * @param {*} theta0 能力值 (θ0)
     * @param {*} thetaU 能力值上界 (θu)
     * @param {*} setP 使用者設定誤判率 (p*)
     * @returns {Object} {items: 新試卷 m: 試卷總試題數 n0: 試卷判斷精熟門檻試題數 wrongRate: 試卷誤判精熟機率}
     */
    function masteryExamAlgS3(thetaL, theta0, thetaU, setP) {
      // 若無結果，回傳最低誤判率
      let minWrongRate = 1;
      // 1.  排序接近 theta0 的試題
      let sortItemIndexs = sortNearInfoTheta(items, theta0);
      for (let i = 1; i <= sortItemIndexs.length; i++) {
        // 2. 計算新 π*l, π*0, π*u
        let newPis = [];
        newPis.push(newThetaToNewPi(sortItemIndexs, i, thetaL));
        newPis.push(newThetaToNewPi(sortItemIndexs, i, theta0));
        newPis.push(newThetaToNewPi(sortItemIndexs, i, thetaU));
        console.log(newPis);
        // 3. 計算誤判率
        let wrongRate = calcWrongRate(newPis, i);
        // 計算最低誤判率
        if (wrongRate[2] < minWrongRate) {
          minWrongRate = wrongRate[2];
        }
        if (wrongRate[2] <= setP) {
          let result = {
            items: sortItemIndexs.slice(0, i),
            m: wrongRate[0],
            n0: wrongRate[1],
            wrongRate: wrongRate[2],
          };
          return result;
        }
      }
      return minWrongRate;
    }
    /**
     * 精熟檢定演算法
     * 整合精熟檢定演算法第二階段與第三階段
     * @param {*} items 題庫
     * @param {*} lower_target 門檻分數下界 (πl)
     * @param {*} target 門檻分數 (π0)
     * @param {*} upper_target 門檻分數上界 (πu)
     * @param {*} decimal 計算小數點位數
     * @param {*} setP 使用者設定誤判率 (p*)
     * @returns {Object} {items: 新試卷 m: 試卷總試題數 n0: 試卷判斷精熟門檻試題數 wrongRate: 試卷誤判精熟機率}
     */
    function masteryExamAlg(
      items,
      lower_target,
      target,
      upper_target,
      decimal,
      setP
    ) {
      let result = masteryExamAlgS2(
        items,
        lower_target,
        target,
        upper_target,
        decimal,
        setP
      );
      console.log(result);
      return masteryExamAlgS3(result[0], result[1], result[2], result[3]);
    }
    console.log(masteryExamAlg(items, 0.7, 0.8, 0.9, 3, 0.1));
  });
});
