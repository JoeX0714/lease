<template>
  <van-card @click="goRoomDetail" class="rounded-xl shadow">
    <!--      title-->
    <template #title>
      <slot name="title">
        <!--      标题-->
        <span class="text-[14px] font-bold">{{
          `${data.apartmentInfo?.name || ""} ${data.roomNumber}房间`
        }}</span>
      </slot>
    </template>
    <!--      desc-->
    <template #desc>
      <slot name="desc">
        <div>
          <span class="text-[12px] --van-gray-6">{{
            `${data?.apartmentInfo?.provinceName || ""} ${
              data?.apartmentInfo?.cityName || ""
            } ${data?.apartmentInfo?.districtName || ""}`
          }}</span>
        </div>
      </slot>
    </template>
    <!--    price-->
    <template #price>
      <slot name="price">
        <!--      价格-->
        <span class="text-red-500 text-[14px]">￥</span>
        <span class="text-red-500 text-[20px]">{{ data.rent }}</span>
      </slot>
    </template>
    <!--    thumb-->
    <template #thumb>
      <slot name="thumb">
        <van-image
          class="w-full h-full object-cover"
          :src="data.graphVoList?.[0]?.url || '失败'"
        >
          <template v-slot:error>加载失败</template>
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
      </slot>
    </template>
    <!--    tags-->
    <template #tags>
      <slot name="tags">
        <van-tag
          class="last:mr-0 mr-[5px]"
          plain
          v-for="item in data?.labelInfoList"
          :key="item.id"
          type="primary"
          >{{ item.name }}
        </van-tag>
      </slot>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import type { RoomInterface } from "@/api/search/types";
import type { PropType } from "vue";
import { useRouter } from "vue-router";
//实际上只需要这些属性
// export interface RoomCardDataProps
//   extends Pick<
//     RoomInterface,
//     "id" | "roomNumber" | "rent" | "graphVoList" | "labelInfoList"
//   > {
//   apartmentInfo: Pick<
//     RoomInterface["apartmentInfo"],
//     "name" | "provinceName" | "cityName" | "districtName"
//   >;
// }
const router = useRouter();
const props = defineProps({
  // 房间的信息数据
  data: {
    type: Object as PropType<RoomInterface>,
    default: () => ({}),
    readOnly: true
  }
});
// 跳转到房间的详情页面
const goRoomDetail = () => {
  router.push({ path: "/roomDetail", query: { id: props.data.id } });
};
</script>

<style scoped></style>
