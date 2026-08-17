<template>
  <van-skeleton :row="20" :loading="!apartmentDetailInfo?.id">
    <div class="page-container">
      <!--  轮播图-->
      <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item
          v-for="item in apartmentDetailInfo.graphVoList"
          :key="item.url"
        >
          <van-image fit="fill" :src="item.url" width="100vw" height="35vh">
            <template v-slot:error>加载失败</template>
            <template v-slot:loading>
              <van-loading type="spinner" size="20" />
            </template>
          </van-image>
        </van-swipe-item>
      </van-swipe>
      <!--  公寓的信息-->
      <div class="card">
        <!--      标题-->
        <div class="font-bold">
          {{ `${apartmentDetailInfo.name}` }}
        </div>
        <!--      标签-->
        <div class="my-[7px]">
          <van-tag
            class="last:mr-0 mr-[5px]"
            plain
            v-for="item in apartmentDetailInfo.labelInfoList"
            :key="item.id"
            type="primary"
            >{{ item.name }}
          </van-tag>
        </div>
        <!--      价格-->
        <div>
          <span class="text-red-500 text-[16px]">￥</span>
          <span class="text-red-500 text-[18px]"
            >{{ apartmentDetailInfo.minRent }}/月起</span
          >
        </div>
      </div>
      <!--    基本信息-->
      <div class="card">
        <div class="base-info-title py-[4px]">社区介绍</div>
        <div class="my-[5px]">
          <div>
            {{ apartmentDetailInfo.introduction }}
          </div>
        </div>
      </div>
      <!--    配套说明-->
      <div class="card">
        <div class="base-info-title py-[4px]">配套说明</div>
        <div class="my-[5px]">
          <van-row>
            <van-col
              span="4"
              class="my-[3px]"
              v-for="item in apartmentDetailInfo.facilityInfoList"
              :key="item.id"
            >
              <div class="flex flex-col justify-center items-center">
                <SvgIcon :name="item.icon" size="25" />
                <span class="text-center">
                  {{ item.name }}
                </span>
              </div>
            </van-col>
          </van-row>
        </div>
      </div>
      <!--    位置详情-->
      <div class="card">
        <div class="base-info-title py-[4px]">位置详情</div>
        <div class="my-[5px]">
          <div class="text-xs mb-[5px]">
            {{ apartmentDetailInfo.addressDetail }}
          </div>
        </div>
        <!--        地图容器-->
        <div id="container" class="w-[85vw] h-[30vh]"></div>
      </div>
      <!--    可选房间列表-->
      <div class="card">
        <div class="base-info-title py-[4px]">可选房间列表</div>
        <div class="mt-[5px]">
          <PullDownRefreshContainer
            :request="getRoomListHandler"
            ref="pullDownRefreshContainerRef"
            class="min-h-[70vh] px-[10px]"
          >
            <template v-if="roomList?.length">
              <RoomCard
                v-for="item in roomList"
                :key="item.id"
                :data="item"
              ></RoomCard>
            </template>
          </PullDownRefreshContainer>
        </div>
      </div>
      <!--    预约看房-->
      <van-sticky :offset-bottom="0" position="bottom">
        <van-button type="primary" block @click="appointmentToViewHandle"
          >预约看房</van-button
        >
      </van-sticky>
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import { getApartmentDetailById, getRoomListByApartmentId } from "@/api/search";
import { onMounted, ref } from "vue";
import type { ApartmentInterface } from "@/api/search/types";
import { useMap } from "@/hooks/useMap";
import poiMarkerRed from "@/assets/poi-marker-red.png";
import RoomCard from "@/components/RoomCard/RoomCard.vue";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import type { ReqPage } from "@/api/types";
import type { RoomInterface } from "@/api/search/types";
import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const route = useRoute();
// 公寓的详情信息
const apartmentDetailInfo = ref<ApartmentInterface>({} as ApartmentInterface);
// 获取公寓的详情信息
const getApartmentDetailHandle = async () => {
  const { data } = await getApartmentDetailById(route.query.id as string);
  apartmentDetailInfo.value = data;
};
// 房间列表
const roomList = ref<RoomInterface[]>([]);
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();
// 分页获取房间的信息
async function getRoomListHandler(pageInfo: ReqPage) {
  //   调用接口
  const { data } = await getRoomListByApartmentId({
    id: route.query.id as string,
    ...pageInfo
  });
  data.records.forEach(item => {
    item.apartmentInfo = apartmentDetailInfo.value;
  });
  console.log("pageInfo.current", pageInfo.current);
  if (pageInfo.current === 1) {
    roomList.value = data.records;
  } else {
    roomList.value = [...roomList.value, ...data.records];
  }
  pullDownRefreshContainerRef.value?.setFinished(
    roomList.value.length >= data.total
  );
}
//#region <高德地图相关>
// 地图实例
const { AMap, initMap } = useMap();
function initMapPage(opts?: { lng: number; lat: number }) {
  console.log("opts", opts);
  const map = new AMap.value.Map("container", {
    zoom: 19, //初始地图级别
    center: [opts?.lng, opts?.lat], //初始地图中心点
    showIndoorMap: false //关闭室内地图
  });
  const icon = new AMap.value.Icon({
    size: new AMap.value.Size(25, 34), // 图标尺寸
    image: poiMarkerRed, // Icon的图像
    imageSize: new AMap.value.Size(25, 34)
  });
  const marker = new AMap.value.Marker({
    icon: icon,
    position: [opts?.lng, opts?.lat],
    anchor: "bottom-center"
  });
  map.add(marker);
  map.setFitView();
}
// 预约看房
const appointmentToViewHandle = () => {
  console.log("appointmentToViewHandle");
  router.push({
    path: "/appointment",
    query: { apartmentId: apartmentDetailInfo.value.id }
  });
};
//#endregion
onMounted(async () => {
  await getApartmentDetailHandle();
  console.log(apartmentDetailInfo.value.longitude);
  await initMap();
  initMapPage({
    lng: +apartmentDetailInfo.value.longitude,
    lat: +apartmentDetailInfo.value.latitude
  });
});
</script>

<style scoped lang="less">
.base-info-title {
  //background-color: var(--van-primary-background-color);
  font-weight: bold;
  //color: white;
}
</style>
