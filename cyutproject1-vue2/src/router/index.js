import Vue from "vue";
import VueRouter from "vue-router";
Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "courses.list",
    component: () => import("@/pages/courses/List"),
  },
  {
    path: "/courses/create",
    name: "courses.create",
    component: () => import("@/pages/courses/Create"),
  },
  {
    path: "/courses/:editCourseId/edit",
    name: "courses.edit",
    component: () => import("@/pages/courses/Edit"),
    props: true,
  },
  {
    path: "/courses/:courseId",
    component: () => import("@/layouts/CourseApp"),
    props: true,
    children: [
      {
        path: "auto-paper",
        name: "auto-papers.generator",
        component: () => import("@/pages/autoPapers/Generator"),
      },
      {
        path: "items/import",
        name: "items.import",
        component: () => import("@/pages/items/Import"),
      },
      {
        path: "items/config/import",
        name: "items.config.import",
        component: () => import("@/pages/items/ImportConfig.vue"),
      },
      {
        path: "items",
        name: "items.list",
        component: () => import("@/pages/items/Board"),
        props: true,
      },
      {
        path: "items/:questionNumber/edit",
        name: "items.edit",
        component: () => import("@/pages/items/Edit"),
        props: true,
      },
      {
        path: "items/assign-topic",
        name: "items.assign-topic",
        component: () => import("@/pages/items/AssignTopic"),
      },
      {
        path: "autoSelectItem",
        name: "AutoSelectItem",
        component: () => import("@/components/AutoSelectItem"),
      },
      {
        path: "papers",
        name: "papers.list",
        component: () => import("@/pages/papers/List"),
      },
      {
        path: "papers/create",
        name: "papers.create",
        component: () => import("@/pages/papers/Create"),
      },
      {
        path: "papers/:paperId/edit",
        name: "papers.edit",
        component: () => import("@/pages/papers/Edit"),
        props: true,
      },
      {
        path: "topics",
        name: "topics.list",
        component: () => import("@/pages/topics/List.vue"),
      },
      {
        path: "topics/import",
        name: "topics.import",
        component: () => import("@/pages/topics/Import.vue"),
      },
    ],
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
