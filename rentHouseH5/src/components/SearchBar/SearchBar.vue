<template>
  <van-dropdown-menu ref="menuRef">
    <van-dropdown-item title="地区">
      <div class="comment-padding">
        <div class="flex flex-row h-[30vh] mt-[5px]">
          <!--        省-->
          <div class="basis-1/3" style="overflow: scroll">
            <div
              class="mt-[5px]"
              :class="{ checked: item.isCheck }"
              @click="clickAreaItemHandle(item, 0)"
              v-for="item in areaInfo.provinceList"
              :key="item.id"
            >
              {{ item.name }}
            </div>
          </div>
          <!--        市-->
          <div class="basis-1/3" style="overflow: scroll">
            <div
              class="mt-[5px]"
              @click="clickAreaItemHandle(item, 1)"
              v-for="item in areaInfo.cityList"
              :class="{ checked: item.isCheck }"
              :key="item.id"
            >
              {{ item.name }}
            </div>
          </div>
          <!--        区-->
          <div class="basis-1/3" style="overflow: scroll">
            <div
              class="mt-[5px]"
              @click="clickAreaItemHandle(item, 2)"
              v-for="item in areaInfo.districtList"
              :class="{ checked: item.isCheck }"
              :key="item.id"
            >
              {{ item.name }}
            </div>
          </div>
        </div>
        <div class="flex comment-padding justify-between my-[10px]">
          <!--        取消-->
          <van-button
            type="default"
            size="small"
            class="basis-2/5"
            round
            @click="onCancelHandle"
          >
            取消
          </van-button>
          <!--        查找-->
          <van-button
            type="primary"
            size="small"
            class="basis-2/5"
            round
            @click="onConfirmHandle"
          >
            查找
          </van-button>
        </div>
      </div>
    </van-dropdown-item>
    <van-dropdown-item title="价格">
      <div class="comment-padding">
        <van-row gutter="10" justify="space-between" class="mt-[5px]">
          <van-col
            class="text-center border p-[4px] mt-[5px] transition-all"
            :class="{ 'other-checked': item.isCheck }"
            @click="rentClickHandle(item)"
            v-for="item in rentInfoList"
            :key="item.label"
            span="7"
            >{{ item.label }}
          </van-col>
        </van-row>
      </div>

      <div class="flex comment-padding justify-between my-[10px]">
        <!--        取消-->
        <van-button
          type="default"
          size="small"
          class="basis-2/5"
          round
          @click="onCancelHandle"
        >
          取消
        </van-button>
        <!--        查找-->
        <van-button
          type="primary"
          size="small"
          class="basis-2/5"
          round
          @click="onConfirmHandle"
        >
          查找
        </van-button>
      </div>
    </van-dropdown-item>
    <van-dropdown-item title="付款方式">
      <div class="comment-padding">
        <van-row justify="space-between" class="mt-[5px]">
          <van-col
            class="text-center border p-[4px] mt-[5px] transition-all"
            :class="{ 'other-checked': item.isCheck }"
            @click="paymentClickHandle(item)"
            v-for="item in paymentInfoList"
            :key="item.label"
            span="7"
            >{{ item.label }}
          </van-col>
        </van-row>
      </div>

      <div class="flex comment-padding justify-between my-[10px]">
        <!--        取消-->
        <van-button
          type="default"
          size="small"
          class="basis-2/5"
          round
          @click="onCancelHandle"
        >
          取消
        </van-button>
        <!--        查找-->
        <van-button
          type="primary"
          size="small"
          class="basis-2/5"
          round
          @click="onConfirmHandle"
        >
          查找
        </van-button>
      </div>
    </van-dropdown-item>
    <van-dropdown-item title="排序">
      <div class="comment-padding">
        <van-row gutter="10" justify="space-around" class="mt-[5px]">
          <van-col
            class="text-center border p-[4px] mt-[5px] transition-all"
            :class="{ 'other-checked': item.isCheck }"
            @click="orderTypeClickHandle(item)"
            v-for="item in orderTypeInfoList"
            :key="item.label"
            span="7"
            >{{ item.label }}
          </van-col>
        </van-row>
      </div>

      <div class="flex comment-padding justify-between my-[10px]">
        <!--        取消-->
        <van-button
          type="default"
          size="small"
          class="basis-2/5"
          round
          @click="onCancelHandle"
        >
          取消
        </van-button>
        <!--        查找-->
        <van-button
          type="primary"
          size="small"
          class="basis-2/5"
          round
          @click="onConfirmHandle"
        >
          查找
        </van-button>
      </div>
    </van-dropdown-item>
  </van-dropdown-menu>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue";
import type { DropdownMenuInstance } from "vant";
import type {
  LabelValueInterface,
  RegionInterface,
  RoomListQueryInterface
} from "@/api/search/types";
import {
  getCityList,
  getDistrictList,
  getPaymentTypeList,
  getProvinceList
} from "@/api/search";
import { SearchOrderType, SearchOrderTypeMap } from "@/enums/constEnums";
const props = defineProps({
  confirmCallback: {
    type: Function,
    default: () => ({})
  }
});
// 组件实例
const menuRef = ref<DropdownMenuInstance>();
const formData = ref<Omit<RoomListQueryInterface, "current" | "size">>({
  // 省市区
  provinceId: "",
  cityId: "",
  districtId: "",
  minRent: "",
  //   最大租金
  maxRent: "",
  //   支付方式
  paymentTypeId: "",
  //   	价格排序方式,可用值:desc,asc
  orderType: ""
});

//#region <省市区查询相关>
const areaInfo = ref({
  // 省份数据
  provinceList: [] as RegionInterface[],
  // 市区数据
  cityList: [] as RegionInterface[],
  // 缓存旧数据
  oldCityListInfo: {
    provinceId: "",
    cityList: [] as RegionInterface[]
  },
  // 区县数据
  districtList: [] as RegionInterface[],
  //   缓存旧数据
  oldDistrictList: {
    cityId: "",
    districtList: [] as RegionInterface[]
  }
});
// 点击省市区
const clickAreaItemHandle = (item: RegionInterface, type: number) => {
  // type 0省 1市 2区
  if (type === 0) {
    if (item.isCheck) {
      item.isCheck = !item.isCheck;
      formData.value.provinceId = "";
      formData.value.cityId = "";
      formData.value.districtId = "";
      areaInfo.value.cityList.forEach(item2 => {
        item2.isCheck = false;
      });
      areaInfo.value.districtList.forEach(item2 => {
        item2.isCheck = false;
      });
      return;
    }
    areaInfo.value.provinceList.forEach(item2 => {
      item2.isCheck = false;
    });
    item.isCheck = !item.isCheck;
    formData.value.provinceId = item.id;
    provinceChangeCallback();
    //   设置市区列表
  } else if (type === 1) {
    if (item.isCheck) {
      item.isCheck = !item.isCheck;
      formData.value.cityId = "";
      formData.value.districtId = "";
      areaInfo.value.districtList.forEach(item2 => {
        item2.isCheck = false;
      });
      return;
    }
    areaInfo.value.cityList.forEach(item2 => {
      item2.isCheck = false;
    });
    item.isCheck = !item.isCheck;
    formData.value.cityId = item.id;
    cityChangeCallback();
  } else if (type === 2) {
    areaInfo.value.districtList.forEach(item2 => {
      if (item2.id !== item.id) {
        item2.isCheck = false;
      } else {
        item.isCheck = !item.isCheck;
      }
    });
    formData.value.districtId = item.isCheck ? item.id : "";
  }

  console.log(item);
};

// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList();
    areaInfo.value.provinceList = data;
  } catch (error) {
    console.log(error);
  }
}

// 获取城市
async function getCityListHandle(
  provinceId: number | string = formData.value.provinceId
) {
  if (!provinceId) return;
  try {
    // 如果存在缓存的旧数据,使用旧数据
    if (
      areaInfo.value.oldCityListInfo.provinceId === provinceId &&
      areaInfo.value.oldCityListInfo.cityList.length
    ) {
      areaInfo.value.cityList = JSON.parse(
        JSON.stringify(areaInfo.value.oldCityListInfo.cityList)
      );
      return;
    }
    const { data } = await getCityList(provinceId);
    // 缓存旧数据
    areaInfo.value.oldCityListInfo.provinceId = provinceId as string;
    areaInfo.value.oldCityListInfo.cityList = JSON.parse(JSON.stringify(data));
    areaInfo.value.cityList = data;
  } catch (error) {
    console.log(error);
  }
}

// 获取区域
async function getDistrictListHandle(
  cityId: number | string = formData.value.cityId
) {
  if (!cityId) return;
  try {
    // 如果存在缓存的旧数据,使用旧数据
    if (
      areaInfo.value.oldDistrictList.cityId === cityId &&
      areaInfo.value.oldDistrictList.districtList.length
    ) {
      areaInfo.value.districtList = JSON.parse(
        JSON.stringify(areaInfo.value.oldDistrictList.districtList)
      );
      return;
    }
    const { data } = await getDistrictList(cityId);
    // 缓存旧数据
    areaInfo.value.oldDistrictList.cityId = cityId as string;
    areaInfo.value.oldDistrictList.districtList = JSON.parse(
      JSON.stringify(data)
    );
    areaInfo.value.districtList = data;
  } catch (error) {
    console.log(error);
  }
}

// 重置市数据
function resetCity() {
  formData.value.cityId = "";
  areaInfo.value.cityList = [];
}

// 重置区数据
function resetDistrict() {
  formData.value.districtId = "";
  areaInfo.value.districtList = [];
}

// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = formData.value.provinceId;
  if (provinceId) {
    resetCity();
    resetDistrict();
    await getCityListHandle(provinceId);
  }
};
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = formData.value.cityId;
  if (cityId) {
    resetDistrict();
    await getDistrictListHandle(cityId);
  }
};

//#endregion
//#region <价格>
// 价格数据
const rentInfoList = ref<LabelValueInterface<(number | string)[]>[]>([
  {
    label: "不限",
    value: ["", ""],
    isCheck: true
  },
  {
    label: "0-1500元",
    value: [0, 1500],
    isCheck: false
  },
  {
    label: "1500-3000元",
    value: [1500, 3000],
    isCheck: false
  },
  {
    label: "3000-6000元",
    value: [3000, 6000],
    isCheck: false
  },
  {
    label: "6000-9000元",
    value: [6000, 9000],
    isCheck: false
  },

  {
    label: "9000-12000元",
    value: [9000, 12000],
    isCheck: false
  }
]);

// 价格点击
function rentClickHandle(item: LabelValueInterface<(number | string)[]>) {
  rentInfoList.value.forEach(item2 => {
    if (item.label === item2.label) {
      item.isCheck = !item.isCheck;
      formData.value.minRent = item.isCheck ? item.value[0] : "";
      formData.value.maxRent = item.isCheck ? item.value[1] : "";
    } else {
      item2.isCheck = false;
    }
  });
}

//#endregion
//#region <付款方式>
// 价格数据
const paymentInfoList = ref<LabelValueInterface[]>([]);

// 获取支付方式列表
async function getPaymentListHandle() {
  try {
    const { data } = await getPaymentTypeList();

    paymentInfoList.value =
      data?.map(item => ({
        label: item.name,
        value: item.id,
        isCheck: false
      })) || [];
  } catch (error) {
    console.log(error);
  }
}

// 价格点击
function paymentClickHandle(item: LabelValueInterface) {
  paymentInfoList.value.forEach(item2 => {
    if (item.label === item2.label) {
      item.isCheck = !item.isCheck;
      formData.value.paymentTypeId = item.isCheck ? item.value : "";
    } else {
      item2.isCheck = false;
    }
  });
}

//#endregion
//#region <排序>
// 价格数据
const orderTypeInfoList = ref<LabelValueInterface[]>(
  SearchOrderTypeMap.map(item => ({
    ...item,
    isCheck: false
  }))
);
// 价格点击
function orderTypeClickHandle(item: LabelValueInterface) {
  orderTypeInfoList.value.forEach(item2 => {
    if (item.label === item2.label) {
      item.isCheck = !item.isCheck;
      formData.value.orderType = item.isCheck
        ? (item.value as SearchOrderType)
        : "";
    } else {
      item2.isCheck = false;
    }
  });
}

//#endregion
const onConfirmHandle = () => {
  // itemRef.value?.toggle();
  // 或者
  menuRef.value?.close();
  //   请求接口
  console.log(formData.value);
  // getRoomList(formData.value);
  console.log("props.confirmCallback", !!props.confirmCallback);
  props.confirmCallback && props.confirmCallback(formData.value);
};
// 取消
const onCancelHandle = () => {
  menuRef.value?.close();
};
onMounted(() => {
  // 获取省份列表
  getProvinceListHandle();
  // 获取支付方式列表
  getPaymentListHandle();
});
</script>

<style scoped lang="less">
// 地区选择
.checked {
  color: var(--van-primary-color);
}

// 价格选择
.other-checked {
  background-color: var(--van-primary-color);
  color: var(--van-white);
}
</style>
