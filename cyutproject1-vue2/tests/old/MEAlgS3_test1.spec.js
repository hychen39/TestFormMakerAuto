/* eslint-disable */
import { round, ceil, combinations } from "mathjs";
import items from "../unit/data/items6.json";

describe("MEAtest", () => {
  it("MEAS3_test1", () => {
    // 3-1 找出接近 theta0 的 item
    // input: items, theta0
    // output: 排序後的 items
    function sortNearInfoTheta(items, theta0) {
      if (items.length < 5) {
        return -1;
      }
      let nearMaxInfoThetaItemIndex = [];
      for (let i = 0; i < items.length; i++) {
        nearMaxInfoThetaItemIndex.push(items[i]);
        nearMaxInfoThetaItemIndex[i].dist = round(Math.abs(theta0 - items[i].maxInfoTheta), 3);
      }
      //items.splice(index, 1);
      return nearMaxInfoThetaItemIndex.sort((a, b) => a.dist - b.dist);
    }
    // 3-2 將 (θl, θ0, θu) -> (π*l, π*0, π*u)，計算新題庫的得分 π*
    // input: 排序後的 items, 試題數量 n, θ
    // output: n個試題下的 π*
    function newThetaToNewPi(newItems, number, theta) {
      let averageCorrectRate = 0;
      for (let i = 0; i < number; i++) {
        averageCorrectRate +=
        round(newItems[i].c + (1 - newItems[i].c) / (1 + Math.exp(-1.7 * newItems[i].a * (theta - newItems[i].b))), 3);
      }
      return round((averageCorrectRate / number), 3);
    };
    // 3-3 計算誤判率 Pm(n0)
    // input: 能力值下界(θl), 能力值(θ0), 能力值上界(θu), 設定誤判率(p*)
    // output: {items: [新試卷], data: [試題數 m, 門檻試題數 n0, 試卷誤判率 Pm(n0)]}
    function calcWrongRate(Pis, m) {
      let n0 = ceil(m * Pis[1], 0)
      let alpha = 0, beta = 0;
      // 計算 Pl(n0)=1-α(n0)
      for (let i = 0; i <= n0 - 1; i++) {
        alpha += combinations(m, i) * (Pis[0]**i) * ((1-Pis[0])**(m - i))
      }
      alpha = round(1 - alpha, 3)
      // 計算 Pu(n0)=1-β(n0)
      for (let i = n0; i <= m; i++) {
        beta += combinations(m, i) * (Pis[2]**i) * ((1-Pis[2])**(m - i))
      }
      beta = round(1 - beta, 3)
      console.log("m = " + m + ", n0 = " + n0 + ", Pl(n0) = " + alpha + ", Pu(n0) = " + beta);
      return [m, n0, Math.max(alpha, beta)]
    }
    // 精熟檢定演算法-第三階段
    // input: Pis [π*l, π*0, π*u], 試題數 m
    // output: [試題數 m, 門檻試題數 n0, 試卷誤判率 Pm(n0)]
    function meAS3(thetaL, theta0, thetaU, setP) {
      // 1.  排序接近 theta0 的試題
      let sortItemIndexs = sortNearInfoTheta(items, theta0);
      // console.log(sortItemIndexs);
      for (let i = 5; i <= sortItemIndexs.length; i++) {
        // 2. 計算新 π*l, π*0, π*u
        let newPis = [];
        newPis.push(newThetaToNewPi(sortItemIndexs, i, thetaL));
        newPis.push(newThetaToNewPi(sortItemIndexs, i, theta0));
        newPis.push(newThetaToNewPi(sortItemIndexs, i, thetaU));
        console.log(newPis);
        // 3. 計算誤判率
        let wrongRate = calcWrongRate(newPis, i);
        if (wrongRate[2] < setP) {
          let result = {
            items: sortItemIndexs.slice(0, i),
            m: wrongRate[0],
            n0: wrongRate[1],
            wrongRate: wrongRate[2]
          }
          return result;
        }
      }
      return -1;
    }
    console.log(meAS3(1.024, 1.371, 1.796, 0.15));
  });
});
