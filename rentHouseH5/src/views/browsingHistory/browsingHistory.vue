<template>
  <div class="main-container">
    <PullDownRefreshContainer
      :request="getHistoryListHandler"
      ref="pullDownRefreshContainerRef"
      class="min-h-[70vh]"
    >
      <template v-if="roomList?.length">
        <RoomCard v-for="item in roomList" :key="item.id" :data="item">
          <template #tags>
            <div>
              <span class="text-[12px] --van-gray-6">{{
                item.browseTime
              }}</span>
            </div>
          </template>
        </RoomCard>
      </template>
    </PullDownRefreshContainer>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { getHistoryList } from "@/api/search";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import type { ReqPage } from "@/api/types";
import RoomCard from "@/components/RoomCard/RoomCard.vue";
import type { RoomInterface } from "@/api/search/types";
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();
console.log("RoomCard.props", RoomCard);
// 房间列表
// 定义个type
type RoomInterfaceWithBrowseTime = RoomInterface & { browseTime: string };
const roomList = ref<RoomInterfaceWithBrowseTime[]>([]);
// 分页获取房间的信息
async function getHistoryListHandler(pageInfo: ReqPage) {
  //   调用接口
  let { data } = await getHistoryList(pageInfo);
  console.log("pageInfo.current", pageInfo.current);
  let targetRecords = data.records.map(item => {
    return {
      // "id" | "roomNumber" | "rent" | "graphVoList" | "labelInfoList"
      id: item.roomId,
      roomNumber: item.roomNumber,
      rent: item.rent,
      graphVoList: item.roomGraphVoList,
      labelInfoList: [],
      apartmentInfo: {
        //   "name" | "provinceName" | "cityName" | "districtName"
        name: item.apartmentName || "",
        provinceName: item.provinceName || "",
        cityName: item.cityName || "",
        districtName: item.districtName || ""
      },
      //  浏览时间
      browseTime: item.browseTime
    };
  }) as unknown as RoomInterfaceWithBrowseTime[];
  if (pageInfo.current === 1) {
    roomList.value = targetRecords;
  } else {
    roomList.value = [...roomList.value, ...targetRecords];
  }
  pullDownRefreshContainerRef.value?.setFinished(
    roomList.value.length >= data.total
  );
}
</script>
