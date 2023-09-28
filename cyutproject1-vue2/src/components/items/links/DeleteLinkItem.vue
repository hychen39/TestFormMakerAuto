<template>
  <v-dialog v-model="dialog" max-width="500">
    <template v-slot:activator="{ attrs, on }">
      <v-btn color="error" v-on="on" v-bind="attrs">
        {{ $t("items.itemLinks.Unlink") }}
      </v-btn>
    </template>
    <v-card>
      <v-card-title class="text-h5">
        {{ $t("items.itemLinks.Are you sure to unlink the link item?") }}
      </v-card-title>
      <v-card-text>
        <p>
          {{ $t("items.itemLinks.This will be permanent") }}
        </p>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>

        <v-btn v-if="!loading" text @click="dialog = false">
          {{ $t("Cancel") }}
        </v-btn>

        <v-btn
          :loading="loading"
          color="primary"
          @click="onDeletedConfirmation"
        >
          {{ $t("Yes, Delete") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { unlinkItem } from "@/services/linkedItemsApi";
import bus from "@/components/core/bus";

export default {
  name: "DeleteLinkItem",
  props: {
    item: {
      type: Object,
      required: true,
    },
    linkedItem: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      dialog: false,
      loading: false,
    };
  },
  methods: {
    async onDeletedConfirmation() {
      try {
        this.loading = true;
        await unlinkItem(this.item.id, this.linkedItem.linkedItemId);
        this.$emit("fetch");
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("items.itemLinks.Unlinked successfully"),
        });
        this.dialog = false;
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped></style>
