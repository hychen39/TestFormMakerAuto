import axios from "@/services/axios";

export async function getAllItemTypes() {
  const { data } = await axios.get("/linked-types");
  return data;
}

export async function linkedItem(itemId, form) {
  const { data } = await axios.put(`/items/${itemId}/linked`, form);

  return data;
}

export async function unlinkItem(itemId, linkedItemId) {
  const { data } = await axios.delete(
    `/items/${itemId}/linked/${linkedItemId}`
  );
  return data;
}
