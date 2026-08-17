<template>
  <div class="logo-container flex-center">
    <a href="/">
      <img class="logo" alt="logo" :src="logoPng"/>
      <h1 class="title" v-if="!collapse">
        {{ getEnvByName('VITE_APP_TITLE') }}
      </h1>
    </a>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, ref } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { getEnvByName } from '@/utils/getEnv'
import logoPngUrl from '@/assets/images/logo.png'
export default defineComponent({
  methods: { getEnvByName },
  setup() {
    const settingsStore = useSettingsStore()
    const collapse = computed(() => settingsStore.collapse)
    const logoPng = ref(logoPngUrl)
    return { logoPng, collapse }
  },
})
</script>

<style scoped lang="scss">
.logo-container {
  position: relative;
  display: flex;
  justify-content: center;

  // padding-left: 24px;
  height: 60px;
  overflow: hidden;
  line-height: 60px;
  background: transparent;

  .title {
    display: inline-block;
    margin-left: 12px;
    font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    color: #fff;
    vertical-align: middle;
  }

  .logo {
    display: inline-block;
    width: 32px;
    height: 32px;
    vertical-align: middle;
    border-radius: 50%;
  }
}
</style>
