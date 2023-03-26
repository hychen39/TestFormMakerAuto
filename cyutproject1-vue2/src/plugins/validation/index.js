import Vue from "vue";
import i18n from "../i18n";
import {
  configure,
  ValidationProvider,
  ValidationObserver,
  extend,
} from "vee-validate";
import { required, email, between } from "vee-validate/dist/rules";

import VTextFieldWithValidation from "@/components/core/VTextFieldWithValidation";
import VTextareaWithValidation from "@/components/core/VTextareaWithValidation.vue";

configure({
  defaultMessage: (field, values) => {
    return i18n.t(`validation.${values._rule_}`, values);
  },
});

extend("email", email);
extend("required", required);
extend("between", between);

// Register it globally
Vue.component("ValidationProvider", ValidationProvider);
Vue.component("ValidationObserver", ValidationObserver);
Vue.component("VTextFieldWithValidation", VTextFieldWithValidation);
Vue.component("VTextareaWithValidation", VTextareaWithValidation);
