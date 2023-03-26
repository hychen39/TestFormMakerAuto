import axios from "@/services/axios";

export async function getItems(
  courseId,
  search = "",
  pagination = {},
  filters = {}
) {
  const { data } = await axios.get(`/courses/${courseId}/items`, {
    params: {
      search,
      ...pagination,
      ...filters,
    },
  });

  return data;
}

export async function getItemByQuestionNumber(courseId, questionNumber) {
  const { data } = await axios.get(
    `/courses/${courseId}/items/qn/${questionNumber}`
  );
  return data;
}

/**
 * @deprecated
 * @param courseId
 * @param questionNumber
 * @returns {Promise<any>}
 */
export async function getItemHtmlByQn(courseId, questionNumber) {
  const { data } = await axios.get(
    `/courses/${courseId}/item/qn/toHtml/${questionNumber}`
  );
  return data;
}

/**
 * @deprecated
 * @param courseId
 * @param questionNumber
 * @returns {Promise<any>}
 */
export async function getItemHtmlByQnOnlyQuestion(courseId, questionNumber) {
  const { data } = await axios.get(
    `/courses/${courseId}/item/qn/toHtml/q/${questionNumber}`
  );
  return data;
}

export async function create(courseId, item) {
  const { data } = await axios.post(`/items`, {
    ...item,
    course_id: courseId,
  });

  return data;
}

export async function update(id, form) {
  const { data } = await axios.put(`/items/${id}`, {
    ...form,
  });

  return data;
}

export async function destroy(id) {
  const { data } = await axios.delete(`items/${id}`);
  return data;
}
