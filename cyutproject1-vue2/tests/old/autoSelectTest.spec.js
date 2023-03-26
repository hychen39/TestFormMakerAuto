import { createLocalVue, shallowMount } from "@vue/test-utils";
import AutoSelectItem from "@/components/AutoSelectItem.vue";

describe("AutoSelectItem.vue", () => {
  const localVue = createLocalVue();

  it("selectedItemId have right answer?", () => {
    const wrapper = shallowMount(AutoSelectItem, {
      localVue,
    });
    wrapper.vm.autoSelectedList = [
      { id: 1, name: "自表格中選取欄位", value: 0 },
      { id: 2, name: "欄位的運算", value: 0 },
      { id: 3, name: "欄位的串接", value: 0 },
    ];
    wrapper.vm.selectItemAction();
    console.log(wrapper.vm.selectedItemId);
    expect(wrapper.vm.selectedItemId[0] % 5).toBe(1);
    expect(wrapper.vm.selectedItemId[1] % 5).toBe(2);
    expect(wrapper.vm.selectedItemId[2] % 5).toBe(3);
  });
});
