<template>
  <div>
    <van-pull-refresh
      v-bind="useAttrs()"
      v-model="loading"
      @refresh="onRefresh"
    >
      <slot>
        <van-empty ref="searchNotFoundRef" description="搜索不到" />
      </slot>
      <!--      底部加载-->
      <div
        class="text-center h-[50px] pt-[10px]"
        ref="bottomLoadRef"
        id="bottomLoadRef"
        v-show="!searchNotFoundRef && !loading"
      >
        <van-loading v-if="loadingMore && !finished">加载中...</van-loading>
        <span class="finish-text" v-if="finished">没有更多了...</span>
      </div>
      <!--      分割-->
      <div class="h-[50px]"></div>
    </van-pull-refresh>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref, useAttrs, watch } from "vue";
import useIntersectionObserver from "@/hooks/useIntersectionObserver";

const props = defineProps({
  // 外部传入的分页请求
  request: {
    type: Function,
    default: () => ({})
  }
});
// 分页信息
const pageInfo = ref({
  current: 1,
  size: 10
});
// 下拉刷新
const loading = ref(false);
// 触底加载
const loadingMore = ref(false);
// 已加载全部数据
const finished = ref(false);
const bottomLoadRef = ref<HTMLElement | null>(null);
const bottomLoadRefIsVisibility = useIntersectionObserver(bottomLoadRef, {
  threshold: 1
});
const searchNotFoundRef = ref<HTMLElement | null>(null);
// 监听底部加载
watch(bottomLoadRefIsVisibility, val => {
  console.log("bottomLoadRefIsVisibility", val);
  // 执行刷新过程中，不再触发
  if (loading.value) {
    return;
  }
  if (val) {
    onLoad();
  }
});
// 刷新
const onRefresh = async () => {
  //   重置信息
  resetInfo();
  await onLoad();
  loading.value = false;
};
// 触底加载
async function onLoad() {
  //  如果已经加载全部数据，不再触发
  if (finished.value) {
    return;
  }
  loadingMore.value = true;
  //   调用接口
  await request();
  loadingMore.value = false;
}
// 重置信息
function resetInfo() {
  pageInfo.value = {
    current: 1,
    size: 10
  };
  setFinished();
}
// 执行请求
async function request() {
  //   调用接口
  await props.request(pageInfo.value);
  //   如果数据不足一页，说明已经加载全部数据
  pageInfo.value.current++;
}
// 全部加载外部，后续触底不需要再重新请求
function setFinished(flag: boolean = false) {
  finished.value = flag;
}
onMounted(() => {
  request();
});
// 对外暴露
defineExpose({
  onRefresh,
  setFinished
});
</script>

<style scoped>
.finish-text {
  color: var(--van-loading-text-color);
  font-size: var(--van-loading-text-font-size);
}
</style>
