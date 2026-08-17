<template>
  <el-switch
    v-model="themeConfig.isDark"
    @change="onAddDarkChange"
    inline-prompt
    :active-icon="Sunny"
    :inactive-icon="Moon"
  />
</template>

<script setup lang="ts" name="SwitchDark">
import { computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useTheme } from '@/hooks/useTheme'
const settingsStore = useSettingsStore()

const { switchDark } = useTheme()

const themeConfig = computed(() => settingsStore.themeConfig)

const onAddDarkChange = () => {
  switchDark()
}
</script>
