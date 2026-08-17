<template>
  <el-drawer title="主题设置" v-model="drawerVisible" size="300px">
    <el-divider class="divider" content-position="center">全局主题</el-divider>
    <div class="theme-item">
      <span>主题颜色</span>
      <el-color-picker
        v-model="themeConfig.primary"
        :predefine="colorList"
        @change="changePrimary"
      />
    </div>
    <div class="theme-item">
      <span>暗黑模式</span>
      <SwitchDark />
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import mittBus from '@/utils/mittBus'
import { DEFAULT_PRIMARY } from '@/config/config'
import { useSettingsStore } from '@/store/modules/settings'
import { useTheme } from '@/hooks/useTheme'
const { changePrimary } = useTheme()

// 预定义主题颜色
const colorList = [
  DEFAULT_PRIMARY,
  '#DAA96E',
  '#0C819F',
  '#722ed1',
  '#27ae60',
  '#ff5c93',
  '#e74c3c',
  '#fd726d',
  '#f39c12',
  '#9b59b6',
]

const settingsStore = useSettingsStore()
const themeConfig = computed(() => settingsStore.themeConfig)

// 打开主题设置
const drawerVisible = ref(false)
mittBus.on('openThemeDrawer', () => {
  drawerVisible.value = true
})
</script>

<style scoped lang="scss">
.theme-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 14px 0;
}
</style>
