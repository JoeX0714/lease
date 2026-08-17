<template>
  <div class="user-container">
    <div class="user h-[30vh] flex flex-col justify-center items-center">
      <van-image
        v-if="isLoggedIn && userStore.userInfo?.avatarUrl"
        @click="
          showImagePreview([userStore.userInfo.avatarUrl])
        "
        round
        width="30vw"
        height="30vw"
        :src="userStore.userInfo.avatarUrl"
      >
        <template v-slot:error>加载失败</template>
      </van-image>
      <van-icon v-else name="user-o" size="30vw" />
      <div class="mt-[8px] font-bold text-[16px]">
        {{ isLoggedIn ? userStore.userInfo?.nickname || "" : "未登录" }}
      </div>
    </div>
    <div class="main-container flex justify-around mt-[30px]">
      <div
        v-for="item in navList"
        :key="item.path"
        class="flex flex-col justify-center items-center"
        @click="router.push(item.path)"
      >
        <SvgIcon :name="item.icon" size="50" />
        <span>{{ item.name }}</span>
      </div>
    </div>
    <div class="main-container flex justify-center mt-[150px]">
      <van-button type="primary" class="w-[50vw]" @click="accountHandle"
        >{{ isLoggedIn ? "退出登录" : "登录" }}</van-button
      >
    </div>
  </div>
</template>
<script setup lang="ts" name="UserCenter">
import { useUserStore } from "@/store/modules/user";
import { showImagePreview } from "vant";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
console.log("router.currentRoute.value.path", router);
const navList = ref([
  {
    icon: "历史",
    name: "浏览历史",
    path: "/browsingHistory"
  },
  {
    icon: "预约",
    name: "我的预约",
    path: "/myAppointment"
  },
  {
    icon: "合同",
    name: "我的租约",
    path: "/myAgreement"
  }
]);
const userStore = useUserStore();
const isLoggedIn = computed(() => Boolean(userStore.token));
const accountHandle = async () => {
  if (!isLoggedIn.value) {
    await router.push("/login");
    return;
  }
  await userStore.Logout();
};
console.log(userStore);
onMounted(async () => {
  if (!userStore.token) {
    userStore.resetUserStore();
    return;
  }
  await userStore.GetInfoAction();
});
</script>

<style scoped lang="less">
.user {
  background: var(--van-primary-background-color);
}
</style>
