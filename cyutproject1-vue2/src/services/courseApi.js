import axios from "@/services/axios";

export async function getCourses(search = "", options = {}) {
  const { data } = await axios.get("/courses", {
    params: {
      search,
      page: options.page,
      itemsPerPage: options.itemsPerPage === -1 ? 99999 : options.itemsPerPage,
    },
  });
  return data;
}

export async function createCourse(form) {
  const { data } = await axios.post(`/courses`, form);
  return data;
}

export async function getCourse(id) {
  const { data } = await axios.get(`/courses/${id}`);
  return data;
}

export async function updateCourse(id, form) {
  const { data } = await axios.put(`/courses/${id}`, form);
  return data;
}

export async function deleteCourse(id) {
  const { data } = await axios.delete(`/courses/${id}`);
  return data;
}
