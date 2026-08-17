<template>
  <van-button v-bind="$attrs" :loading="loading" @click="loadingClick"
    ><slot></slot
  ></van-button>
</template>
<script setup lang="ts">
/**
 * @description: 具有loading状态的按钮
 * @param {Function} loadingClick 点击事件，传入原有click事件触发的方法即可
 * @param {https://vant-contrib.gitee.io/vant/#/zh-CN/button} 支持其中所有的属性
 */
import { ref } from "vue";
const props = defineProps({
  loadingClick: {
    type: Function,
    default: () => Promise<void>
  }
});
// 按钮loading状态
const loading = ref(false);
async function loadingClick() {
  try {
    loading.value = true;
    await props.loadingClick();
    loading.value = false;
  } catch (error) {
    console.error(error);
    loading.value = false;
  }

  // 自定义的点击事件处理逻辑
}
</script>

<style scoped lang="scss"></style>
