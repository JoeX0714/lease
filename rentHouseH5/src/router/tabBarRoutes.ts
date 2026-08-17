import type { RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    name: "Search",
    path: "/search",
    component: () => import("@/views/search/search.vue"),
    meta: {
      title: "找房",
      icon: "search",
      isTabBar: true
    }
  },
  {
    path: "/ai",
    name: "AiAssistant",
    component: () => import("@/views/ai/ai.vue"),
    meta: {
      title: "AI助手",
      icon: "chat-o",
      isTabBar: true
    }
  },
  {
    path: "/myRoom",
    name: "MyRoom",
    component: () => import("@/views/myRoom/myRoom.vue"),
    meta: {
      title: "我的房间",
      icon: "home-o",
      isTabBar: true
    }
  },
  {
    path: "/message",
    name: "Message",
    component: () => import("@/views/message/message.vue"),
    meta: {
      title: "消息",
      icon: "comment-o",
      isTabBar: true
    }
  },
  {
    path: "/userCenter",
    name: "UserCenter",
    component: () => import("@/views/userCenter/userCenter.vue"),
    meta: {
      title: "个人中心",
      icon: "user-o",
      isShowNavBar: true,
      isTabBar: true
    }
  }
];

export default routes;
