<template>
  <div v-if="course">
    <v-fade-transition hide-on-leave>
      <router-view :course="course"></router-view>
    </v-fade-transition>
  </div>
</template>

<script>
import { getCourse } from "@/services/courseApi";

export default {
  name: "CourseApp",
  props: {
    courseId: {
      type: [String, Number],
    },
  },
  asyncComputed: {
    course: {
      async get() {
        const { data } = await getCourse(this.courseId);
        this.$store.commit("setCurrentCourse", data);
        return data;
      },
      watch: ["courseId"],
    },
  },
};
</script>

<style scoped></style>
