<template>
  <section class="app-mian-height">
    <router-view v-slot="{ Component, route }" v-if="isShow" :key="key">
      <transition appear name="fade-transform" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </section>
</template>

<script lang="ts">
import { computed, defineComponent, nextTick, ref, watch } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { useRoute } from 'vue-router'

export default defineComponent({
  setup() {
    const settingsStore = useSettingsStore()
    const isShow = ref(true)
    const route = useRoute()
    const key = computed(() => {
      return route.path + Math.random()
    })
    watch(
      () => settingsStore.refresh,
      () => {
        isShow.value = false
        nextTick(() => {
          isShow.value = true
        })
      },
    )
    return { isShow, key }
  },
})
</script>

<style scoped lang="scss">
.app-mian-height {
  min-height: $base-app-main-height;

  // padding: 20px;
  background-color: inherit;
}
</style>
