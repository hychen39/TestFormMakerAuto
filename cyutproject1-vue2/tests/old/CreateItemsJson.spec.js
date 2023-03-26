import { sqrt } from "mathjs";

describe("MEAtest", () => {
  it("MEAtest1", () => {
    function CreateItems(amount) {
      let items = [];
      for (let i = 1; i <= amount; i++) {
        let item = {
          Itemid: i,
          a: parseFloat(
            (Math.round(Math.random() * 1999) / 1000 + 0.001).toFixed(3)
          ),
          b: parseFloat(
            (
              Math.round(Math.random() * 3000) / 1000 -
              Math.round(Math.random() * 3000) / 1000
            ).toFixed(3)
          ),
          c: (Math.round(Math.random() * 19) + 1) / 100,
          maxInfoTheta: 0.0,
        };
        item.maxInfoTheta = parseFloat(
          (
            item.b +
            (1 / 1.7) * item.a * Math.log(0.5 * (1 + sqrt(1 + 8 * item.c)))
          ).toFixed(3)
        );
        items.push(item);
      }
      return items;
    }
    console.log(CreateItems(100));
  });
});
