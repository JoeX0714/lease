<template>
  <van-skeleton :row="15" :loading="loading">
    <div class="p-[10px]">
      <div
        v-for="item in notificationList"
        :key="item.id"
        class="main-container bg-white rounded-xl shadow mb-[10px] p-[12px]"
        @click="showNotificationHandle(item)"
      >
        <div class="flex justify-between items-center">
          <div class="font-bold text-[15px]">{{ item.title }}</div>
          <van-tag v-if="!item.isRead" type="danger" size="medium">未读</van-tag>
        </div>
        <div class="text-[13px] --van-gray-6 mt-[4px] van-ellipsis">
          {{ item.content }}
        </div>
        <div class="text-[12px] --van-gray-6 mt-[4px]">{{ item.createTimeStr }}</div>
      </div>
      <van-empty
        v-if="!loading && notificationList?.length <= 0"
        description="暂无通知"
      />
    </div>
  </van-skeleton>

  <van-dialog
    v-model:show="dialogVisible"
    :title="currentTitle"
    :show-cancel-button="false"
  >
    <div class="p-[15px] text-[14px]">{{ currentContent }}</div>
  </van-dialog>
</template>

<script setup lang="ts" name="Message">
import { onActivated, ref } from "vue";
import {
  getNotificationList,
  readNotificationById
} from "@/api/notification";
import type { NotificationItemInterface } from "@/api/notification/types";

const loading = ref(true);
const notificationList = ref<NotificationItemInterface[]>([]);
const dialogVisible = ref(false);
const currentTitle = ref("");
const currentContent = ref("");

async function getNotificationListHandle() {
  try {
    const { data } = await getNotificationList();
    notificationList.value = data;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
}

async function showNotificationHandle(item: NotificationItemInterface) {
  currentTitle.value = item.title;
  currentContent.value = item.content;
  dialogVisible.value = true;
  if (!item.isRead) {
    try {
      await readNotificationById(item.id);
      item.isRead = true;
    } catch (error) {
      console.log(error);
    }
  }
}

// 每次进入消息页面重新获取最新通知（keep-alive 下用 onActivated）
onActivated(getNotificationListHandle);
</script>

<style lang="less" scoped></style>
