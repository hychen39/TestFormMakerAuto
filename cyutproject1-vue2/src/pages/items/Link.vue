<template>
  <v-container fluid>
    <v-card>
      <v-app-bar dark color="#555555">
        <v-toolbar-title>
          <span v-if="item">
            {{ item.questionNumber }}
            {{ item.title }}
          </span>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-dialog v-model="previewDialog">
          <template v-slot:activator="{ attrs, on }">
            <v-btn color="info" class="mx-3" v-on="on" v-bind="attrs">
              Preview
            </v-btn>
          </template>
          <preview-card
            :item="item"
            :closeable="true"
            @close="previewDialog = false"
          ></preview-card>
        </v-dialog>

        <v-btn
          v-if="!loading"
          :to="{ name: 'items.list', query: $route.query }"
          color="teal"
        >
          {{ $t("Back") }}
        </v-btn>
      </v-app-bar>
      <v-card-text
        v-if="item"
        class="overflow-auto"
        style="max-height: calc(100vh - 122px)"
      >
        <v-slide-y-transition hide-on-leave>
          <v-card v-if="createLinkedItemMode" flat outlined class="mb-5">
            <v-card-title>
              <span>
                {{ $t("items.itemLinks.Create Linked Items") }}
              </span>
            </v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <v-row class="align-center">
                <v-col cols="12" md="4">
                  <v-select
                    :label="$t('items.itemLinks.Linked Item Type')"
                    :items="itemTypes"
                    v-model="form.link_type_id"
                    item-text="name"
                    item-value="id"
                  >
                  </v-select>
                </v-col>
                <v-col cols="12" md="8">
                  <v-autocomplete
                    :label="$t('items.itemLinks.Linked Item')"
                    :items="linkedItems"
                    v-model="form.linked_item_id"
                    item-value="id"
                    item-text="_text"
                    deletable-chips
                    small-chips
                  >
                    <template v-slot:item="data">
                      <v-list-item-content>
                        <v-list-item-title style="white-space: normal">
                          <v-chip>{{ data.item.questionNumber }}</v-chip>
                          {{ data.item.title }}
                        </v-list-item-title>
                      </v-list-item-content>
                    </template>
                  </v-autocomplete>
                </v-col>
              </v-row>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="teal" dark @click="createLinkedItemMode = false">
                {{ $t("Cancel") }}
              </v-btn>
              <v-btn
                color="primary"
                :loading="loading"
                :disabled="!form.linked_item_id || !form.link_type_id"
                @click="onSubmit"
              >
                {{ $t("Submit") }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-slide-y-transition>
        <v-row>
          <v-col cols="12">
            <DataTable
              :value="links"
              ref="dataTable"
              class="p-datatable-sm"
              columnResizeMode="fit"
              showGridlines
              removableSort
            >
              <template #header>
                <v-row class="align-center py-2 pr-3">
                  <v-col>
                    <h5 class="m-0">
                      {{ $t("items.itemLinks.Linked Items") }}
                    </h5>
                  </v-col>
                  <v-spacer></v-spacer>
                  <v-btn
                    color="primary"
                    small
                    :disabled="createLinkedItemMode"
                    @click="createLinkedItemMode = true"
                  >
                    <v-icon> mdi-plus</v-icon>
                    {{ $t("items.itemLinks.Create Linked Items") }}
                  </v-btn>
                </v-row>
              </template>
              <Column
                field="questionNumber"
                :header="$t('items.ID')"
                :sortable="true"
                :style="{ width: '10%' }"
              ></Column>
              <Column
                field="title"
                :header="$t('items.Title')"
                :sortable="true"
                :style="{ width: '60%' }"
              >
                <template v-slot:body="{ data }">
                  <div class="text-break text-wrap">
                    {{ data.title }}
                  </div>
                </template>
              </Column>
              <Column
                field="linkType.name"
                :header="$t('items.itemLinks.Linked Item Type')"
                :sortable="true"
                :style="{ width: '10%' }"
              >
              </Column>
              <Column
                field="linkType.action"
                :header="$t('Actions')"
                :sortable="false"
                :style="{ width: '10%' }"
              >
                <template v-slot:body="{ data }">
                  <DeleteLinkItem
                    :item="item"
                    :linkedItem="data"
                    @fetch="fetch"
                  ></DeleteLinkItem>
                </template>
              </Column>
            </DataTable>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import { getItemByQuestionNumber, getItems, links } from "@/services/ItemApi";
import bus from "@/components/core/bus";
import { getAllItemTypes, linkedItem } from "@/services/linkedItemsApi";
import collect from "collect.js";
import PreviewCard from "@/components/items/PreviewCard.vue";
import DeleteLinkItem from "@/components/items/links/DeleteLinkItem.vue";

export default {
  name: "Link",
  components: { DeleteLinkItem, PreviewCard },
  props: {
    course: {
      type: Object,
      required: true,
    },
    questionNumber: {
      type: [String],
      required: true,
    },
  },
  data() {
    return {
      previewDialog: false,
      createLinkedItemMode: false,
      loading: false,
      item: null,
      itemSearch: null,
      links: [],
      form: {
        linked_item_id: null,
        link_type_id: null,
      },
    };
  },
  computed: {
    linkedItems() {
      const links = collect(this.links).pluck("linkedItemId").toArray();
      return collect(this.linkedItemsSource)
        .reject(
          (data) => data.id === this.item?.id || links.indexOf(data.id) > -1
        )
        .map((data) => ({
          ...data,
          _text: `${data.questionNumber} ${data.title}`,
        }))
        .values()
        .all();
    },
  },
  asyncComputed: {
    itemTypes: {
      async get() {
        const { data } = await getAllItemTypes();
        return data;
      },
      default: [],
    },
    linkedItemsSource: {
      async get() {
        const { data } = await getItems(this.course.id, this.itemSearch, {
          page: 1,
          itemsPerPage: 999999,
        });
        return data;
      },
      immediate: false,
      default: [],
    },
  },
  methods: {
    async fetchLinks() {
      const { data } = await links(this.item.id);
      this.links = data;
    },
    async fetch() {
      try {
        const { data } = await getItemByQuestionNumber(
          this.course.id,
          this.questionNumber
        );
        this.item = data;
        await this.fetchLinks();
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      }
    },
    async onSubmit() {
      try {
        await linkedItem(this.item.id, this.form);
        bus.$emit("alert-message", {
          type: "success",
          message: this.$t("items.itemLinks.Linked item successfully"),
        });
        this.form = {
          linked_item_id: null,
          link_type_id: null,
        };
        await this.fetchLinks();
      } catch (e) {
        bus.$emit("alert-message", {
          type: "error",
          message: e.message,
        });
      }
    },
  },
  async mounted() {
    await this.fetch();
  },
};
</script>

<style scoped></style>
