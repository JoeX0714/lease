<template>
  <van-nav-bar
    v-if="isShowNavBar"
    fixed
    placeholder
    :title="route.meta.title as string"
    :left-arrow="isShowBackArrow"
    @click-left="onClickLeft"
    @click-right="onClickRight"
  >
    <template #right>
      <svg-icon class="text-[18px]" :name="useDarkMode() ? 'light' : 'dark'" />
    </template>
  </van-nav-bar>
</template>
<script setup lang="ts">
import { useDarkMode, useToggleDarkMode } from "@/hooks/useToggleDarkMode";
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
const onClickRight = () => {
  useToggleDarkMode();
};
// meta.isShowNavBar === true 才显示顶部导航栏
const isShowNavBar = computed(() => route.meta.isShowNavBar === true);
// 一级 Tab 页不显示返回箭头，非 Tab 二级页面显示
const isShowBackArrow = computed(
  () => isShowNavBar.value && route.meta.isTabBar !== true
);
// 优先 router.back()，没有历史记录则回退到 meta.activeTab 对应的一级页
const onClickLeft = () => {
  const state = window.history.state as { back?: string | null } | null;
  if (state?.back) {
    router.back();
  } else {
    router.replace(
      (route.meta.activeTab as string) || "/search"
    );
  }
};
</script>

<style scoped></style>
