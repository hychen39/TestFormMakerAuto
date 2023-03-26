import axios from "@/services/axios";

export async function getTopics(courseId) {
  const { data } = await axios.get(`/courses/${courseId}/topics`);
  return data;
}

export async function getTopic(id) {
  const { data } = await axios.get(`/topic/${id}`);
  return data;
}

export async function getItemsByTopic(
  id,
  except = [],
  search = "",
  pagination = {}
) {
  const { data } = await axios.post(
    `/topic/${id}/items`,
    {
      except: except,
    },
    {
      params: {
        search,
        ...pagination,
        itemsPerPage:
          pagination.itemsPerPage === -1 ? 999999 : pagination.itemsPerPage,
      },
    }
  );
  return data;
}

export async function createTopic(courseId, form) {
  const { data } = await axios.post(`/topics`, {
    course_id: courseId,
    ...form,
  });

  return data;
}

export async function updateTopic(id, form) {
  const { data } = await axios.put(`/topics/${id}`, form);

  return data;
}

export async function deleteTopic(id) {
  const { data } = await axios.delete(`/topics/${id}`);

  return data;
}

export async function importTopics(courseId, form) {
  const { data } = await axios.post(`/courses/${courseId}/topics/import`, form);

  return data;
}

export async function getItemsByTopicIds(courseId, form) {
  const { data } = await axios.post(
    `/courses/${courseId}/topics/multiple/items`,
    form
  );

  return data;
}

export async function getItemCountInTopic(courseId) {
  const { data } = await axios.get(`/courses/${courseId}/topics/item/count`);

  return data;
}
