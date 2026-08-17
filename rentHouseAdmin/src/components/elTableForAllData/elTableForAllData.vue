<template>
  <el-table v-bind="$attrs" :data="showData" style="width: 100%" border>
    <slot></slot>
  </el-table>
  <!--        分页-->
  <el-pagination
    v-model:current-page="pageInfo.currentPage"
    v-model:page-size="pageInfo.pageSize"
    :page-sizes="[10, 20, 30]"
    layout="prev, pager, next,sizes,total"
    :total="pageInfo.total"
    :style="{ marginTop: '20px' }"
    :hide-on-single-page="true"
    @size-change="sizeChange"
    @current-change="currentChange"
  />
</template>
<script setup lang="ts">
import { usePage } from '@/hooks/usePagination'
import { PropType, ref, watch } from 'vue'
const props = defineProps({
  data: {
    type: Array as PropType<any[]>,
    required: true,
    default: () => [],
  },
})
const showData = ref([] as typeof props.data)
// 分页hooks
const { pageInfo, sizeChange, currentChange } = usePage({
  callback: getShowDataInfo,
})
// 获取翻页数据
function getShowDataInfo() {
  // 获取当前的分页参数
  const { currentPage, pageSize } = pageInfo
  // 初始化总页数
  pageInfo.total = props.data.length
  // 计算当前页的数据
  const start = (currentPage - 1) * pageSize
  const end = currentPage * pageSize
  showData.value = props.data.slice(start, end)
}
// 页面初始化方法
// 监视数据变化
watch(
  () => props.data,
  () => {
    init()
  },
  { immediate: true },
)
function init() {
  pageInfo.currentPage = 1
  getShowDataInfo()
}
</script>

<style scoped lang="scss"></style>
