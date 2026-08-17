<template>
  <van-skeleton :row="20" :loading="!roomDetailInfo?.id">
    <div class="page-container">
      <!--  轮播图-->
      <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item
          v-for="item in roomDetailInfo.graphVoList"
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

      <!--  房间的信息-->
      <div class="card">
        <div class="">
          <!--      标题-->
          <div class="font-bold">
            {{
              `${roomDetailInfo.apartmentItemVo.name} ${roomDetailInfo.roomNumber}房间`
            }}
          </div>
          <!--      标签-->
          <div class="my-[7px]">
            <van-tag
              class="last:mr-0 mr-[5px]"
              plain
              v-for="item in roomDetailInfo.labelInfoList"
              :key="item.id"
              type="primary"
              >{{ item.name }}
            </van-tag>
          </div>
          <!--      价格-->
          <div>
            <span class="text-red-500 text-[16px]">￥</span>
            <span class="text-red-500 text-[18px]"
              >{{ roomDetailInfo.rent }}/月</span
            >
          </div>
        </div>
      </div>

      <!--    基本信息-->
      <div class="card">
        <div class="base-info-title py-[4px]">基本信息</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col
              span="12"
              class="my-[3px]"
              v-for="item in roomDetailInfo.attrValueVoList"
              :key="item.id"
            >
              <van-row>
                <van-col span="6">
                  <van-tag type="primary">{{ item.attrKeyName }}</van-tag>
                </van-col>
                <van-col span="18">
                  {{ item.name }}
                </van-col>
              </van-row>
            </van-col>
            <!--          占位修饰-->
            <van-col
              class="my-[3px]"
              v-if="roomDetailInfo.attrValueVoList?.length % 2 !== 0"
              span="12"
            >
            </van-col>
          </van-row>
        </div>
      </div>
      <!--    配套信息-->
      <div class="card">
        <div class="base-info-title py-[4px]">配套信息</div>
        <div class="my-[5px]">
          <van-row>
            <van-col
              span="4"
              class="my-[3px]"
              v-for="item in roomDetailInfo.facilityInfoList"
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
          <div class="text-xs mb-[5px] w-[300px]">
            {{ roomDetailInfo.apartmentItemVo.addressDetail }}
          </div>
        </div>
        <!--        地图容器-->
        <div id="container" class="w-[85vw] h-[30vh]"></div>
      </div>
      <!--    费用明细-->
      <div class="card">
        <div class="base-info-title py-[4px]">费用明细</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col span="12">
              <van-tag type="primary" size="medium">费用科目</van-tag>
            </van-col>
            <van-col span="12">
              <van-tag type="primary" size="medium">收费标准</van-tag>
            </van-col>
            <template
              v-for="item in roomDetailInfo.feeValueVoList"
              :key="item.id"
            >
              <van-col span="12" class="my-[5px]">
                <span>{{ item.feeKeyName }}</span>
              </van-col>
              <van-col span="12" class="my-[5px]">
                <span>{{ `￥${item.name}${item.unit}` }}</span>
              </van-col>
            </template>
          </van-row>
        </div>
      </div>
      <!--    付款说明-->
      <div class="card">
        <div class="base-info-title py-[4px]">可选付款方式</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col span="12">
              <van-tag type="primary" size="medium">可选付款方式</van-tag>
            </van-col>
            <van-col span="12">
              <van-tag type="primary" size="medium">说明</van-tag>
            </van-col>
            <template
              v-for="item in roomDetailInfo.paymentTypeList"
              :key="item.id"
            >
              <van-col span="12" class="my-[5px]">
                <span>{{ item.name }}</span>
              </van-col>
              <van-col span="12" class="my-[5px]">
                <span>{{ item.additionalInfo }}</span>
              </van-col>
            </template>
          </van-row>
        </div>
      </div>
      <div class="card">
        <div class="base-info-title py-[4px]">可选租期</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col span="12">
              <van-tag type="primary" size="medium">可选租期</van-tag>
            </van-col>
            <van-col span="12">
              <van-tag type="primary" size="medium">说明</van-tag>
            </van-col>
            <template
              v-for="item in roomDetailInfo.leaseTermList"
              :key="item.id"
            >
              <van-col span="12" class="my-[5px]">
                <span>{{ item.monthCount + item.unit }}</span>
              </van-col>
              <van-col span="12" class="my-[5px]">
                <span> 到期可续 </span>
              </van-col>
            </template>
          </van-row>
        </div>
      </div>
      <!--    所属公寓-->
      <div class="card">
        <div class="base-info-title py-[4px]">所属公寓</div>
        <div class="my-[5px] pb-[50px] px-[10px]">
          <ApartmentCard :data="roomDetailInfo.apartmentItemVo"></ApartmentCard>
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
import { getRoomDetailById } from "@/api/search";
import { onMounted, ref } from "vue";
import type { RoomDetailInterface } from "@/api/search/types";
import { useMap } from "@/hooks/useMap";
import poiMarkerRed from "@/assets/poi-marker-red.png";
import ApartmentCard from "@/components/ApartmentCard/ApartmentCard.vue";
import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const route = useRoute();
// 房间的详情信息
const roomDetailInfo = ref<RoomDetailInterface>({} as RoomDetailInterface);
// 获取房间的详情信息
const getRoomDetailHandle = async () => {
  const { data } = await getRoomDetailById(route.query.id as string);
  roomDetailInfo.value = data;
};
//#region <高德地图相关>
// 地图实例
const { AMap, initMap } = useMap();
function initMapPage(opts?: { lng: number; lat: number }) {
  console.log("opts", opts);
  console.log("AMap", AMap.value);
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
    query: {
      apartmentId: roomDetailInfo.value.apartmentId,
      roomId: roomDetailInfo.value.id
    }
  });
};
//#endregion
onMounted(async () => {
  await getRoomDetailHandle();
  console.log(roomDetailInfo.value.apartmentItemVo.longitude);
  await initMap();
  initMapPage({
    lng: +roomDetailInfo.value.apartmentItemVo.longitude,
    lat: +roomDetailInfo.value.apartmentItemVo.latitude
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
