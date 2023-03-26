<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          {{ $t("items.board.Import Item Config") }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
      </v-app-bar>
      <v-card-text>
        <v-file-input
          :loading="importing"
          accept=".xlsx"
          :label="$t('items.board.Import Item Config') + ' (xlsx)'"
          show-size
          small-chips
          v-model="file"
          color="#7199de"
          @change="importParameters"
        >
        </v-file-input>
      </v-card-text>
    </v-card>

    <v-expand-transition>
      <v-card v-if="error" class="mt-3" flat>
        <v-card-text class="px-0">
          <v-alert color="error" dark>
            <div class="title">{{ $t("topics.Import Failed") }}</div>
            <div class="mb-3">
              {{ error }}
            </div>
          </v-alert>
        </v-card-text>
      </v-card>

      <v-card v-if="success" class="mt-3" flat>
        <v-card-text class="px-0">
          <v-alert color="success" dark>
            <div class="title">
              {{ $t("items.board.Imported successfully") }}
            </div>
          </v-alert>
        </v-card-text>
      </v-card>
    </v-expand-transition>
  </v-container>
</template>

<script>
import * as XLSX from "xlsx";
import { getItemByQuestionNumber, update } from "@/services/ItemApi";
import { round, sqrt } from "mathjs";

export default {
  name: "ImportParameter",
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      importing: false,
      file: null,
      success: false,
      error: null,
      loading: false,
    };
  },
  methods: {
    async importParameters() {
      if (!this.file) {
        return;
      }
      this.error = null;
      this.success = false;
      try {
        this.loading = true;
        this.importData = [];
        this.importData = await this.excelToArray(this.file);

        for (let i = 0; i < this.importData.length; i++) {
          const item = (
            await getItemByQuestionNumber(
              this.course.id,
              this.importData[i].questionNumber
            )
          ).data;

          if (item !== null) {
            item.a = this.importData[i].a;
            item.b = this.importData[i].b;
            item.c = this.importData[i].c;
            item.maxInfoTheta = round(
              item.b +
                (1 / 1.7) * item.a * Math.log(0.5 * (1 + sqrt(1 + 8 * item.c))),
              3
            );
            await update(item.id, item);
          }
        }
        this.success = true;
      } catch (e) {
        this.error = e.message;
      } finally {
        this.loading = false;
      }
    },
    excelToArray(file) {
      var reader = new FileReader();

      return new Promise((resolve, reject) => {
        reader.onerror = () => {
          reader.abort();
          reject(new DOMException("Problem parsing input file."));
        };

        reader.onload = (e) => {
          /* Parse data */
          const bstr = e.target.result;
          const wb = XLSX.read(bstr, { type: "binary" });
          /* Get first worksheet */
          const wsname = wb.SheetNames[0];
          const ws = wb.Sheets[wsname];
          /* Convert array of arrays */
          let rowObject = XLSX.utils.sheet_to_row_object_array(ws);
          resolve(rowObject);
        };
        reader.readAsBinaryString(file);
      });
    },
  },
};
</script>

<style scoped></style>
