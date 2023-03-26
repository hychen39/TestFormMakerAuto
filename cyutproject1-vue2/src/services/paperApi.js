import axios from "@/services/axios";

export async function getPapers(courseId, search, options) {
  const { data } = await axios.get(`/courses/${courseId}/papers`, {
    params: {
      search,
      page: options.page,
      itemsPerPage: options.itemsPerPage === -1 ? 99999 : options.itemsPerPage,
    },
  });

  return data;
}

export async function createPaper(courseId, form) {
  const { data } = await axios.post(`/papers`, {
    ...form,
    course_id: courseId,
  });

  return data.data;
}

export async function getPaper(paperId) {
  const { data } = await axios.get(`/papers/${paperId}`);

  return data;
}

export async function updatePaper(paperId, form) {
  const { data } = await axios.put(`/papers/${paperId}`, form);

  return data;
}

export async function deletePaper(paperId) {
  const { data } = await axios.delete(`/papers/${paperId}`);

  return data;
}
