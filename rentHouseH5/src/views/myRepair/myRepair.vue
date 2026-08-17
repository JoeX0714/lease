<template>
  <van-skeleton :row="15" :loading="loading">
    <div class="p-[10px]">
      <van-card
        class="rounded-xl shadow"
        v-for="item in repairList"
        :key="item.id"
      >
        <template #title>
          <h2 class="text-[15px] font-bold">
            {{ item.apartmentName }} {{ item.roomNumber }}房间
          </h2>
        </template>
        <template #desc>
          <div class="text-[12px] --van-gray-6">
            <div>提交时间：{{ item.createTimeStr }}</div>
            <div class="mt-[4px]">
              报修状态：
              <van-tag :type="statusTagType(item.status)" size="medium">
                {{ getLabelByValue(RepairStatusMap, Number(item.status)) }}
              </van-tag>
            </div>
          </div>
        </template>
        <template #footer>
          <van-button size="mini" plain type="primary" @click="showRepairContent(item)">
            查看
          </van-button>
        </template>
      </van-card>
      <van-empty
        v-if="!loading && repairList?.length <= 0"
        description="暂无报修记录"
      />
    </div>
  </van-skeleton>

  <van-dialog
    v-model:show="dialogVisible"
    title="报修信息"
    :show-cancel-button="false"
  >
    <div class="p-[15px] text-[14px]">{{ currentContent }}</div>
  </van-dialog>
</template>

<script setup lang="ts" name="MyRepair">
import { onMounted, ref } from "vue";
import { getMyRepairList } from "@/api/repair";
import type { RepairItemInterface } from "@/api/repair/types";
import {
  getLabelByValue,
  RepairStatus,
  RepairStatusMap
} from "@/enums/constEnums";

const loading = ref(true);
const repairList = ref<RepairItemInterface[]>([]);
const dialogVisible = ref(false);
const currentContent = ref("");

async function getRepairListHandle() {
  try {
    const { data } = await getMyRepairList();
    repairList.value = data;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
}

function showRepairContent(item: RepairItemInterface) {
  currentContent.value = item.repairContent;
  dialogVisible.value = true;
}

function statusTagType(status: RepairStatus) {
  switch (status) {
    case RepairStatus.PENDING:
      return "primary";
    case RepairStatus.COMPLETED:
      return "success";
    case RepairStatus.CANCELED:
      return "default";
    default:
      return "default";
  }
}

onMounted(getRepairListHandle);
</script>

<style lang="less" scoped></style>
