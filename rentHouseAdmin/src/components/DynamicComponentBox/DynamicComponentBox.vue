<!--
 * @Author: cqq 945327638@qq.com
 * @Date: 2023-05-26 18:09:54
 * @LastEditors: cqq 945327638@qq.com
 * @LastEditTime: 2023-05-26 18:47:47
 * @FilePath: \listen-to-books-admin\src\components\DynamicComponentBox\DynamicComponentBox.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <component :is="is" v-bind="$attrs"></component>
</template>
<script setup lang="ts">
defineProps<{
  is: string
}>()
</script>
<style scoped lang="scss"></style>
