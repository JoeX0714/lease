<!--
 * @Description: 
 * @Autor: 李海波
 * @Date: 2023-03-03 13:41:05
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-15 17:20:10
-->
<template>
  <div class="nav-bar-container">
    <el-row :gutter="15">
      <el-col :sm="12" :md="12" :lg="12" :xl="12">
        <div class="left-panel">
          <el-icon class="fold-unfold" @click="handleCollapse">
            <component :is="collapse ? 'Expand' : 'Fold'"></component>
          </el-icon>
          <Breadcrumb />
        </div>
      </el-col>
      <el-col :sm="12" :md="12" :lg="12" :xl="12">
        <div class="right-panel">
          <Refresh />
          <ScreenFull />
          <Settings />
          <User />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import User from './components/User/index.vue'
import Breadcrumb from './components/Breadcrumb/index.vue'
import Refresh from './components/Refresh/index.vue'
import ScreenFull from './components/ScreeFull/index.vue'
import Settings from './components/Settings/index.vue'
export default defineComponent({
  components: {
    Refresh,
    User,
    ScreenFull,
    Breadcrumb,
    Settings,
  },
  setup() {
    const settingsStore = useSettingsStore()

    const collapse = computed(() => settingsStore.collapse)

    function handleCollapse() {
      settingsStore.changeCollapse()
    }
    return {
      collapse,
      handleCollapse,
    }
  },
})
</script>

<style scoped lang="scss">
.nav-bar-container {
  position: relative;
  height: $base-nav-bar-height;
  padding-right: $base-padding;
  padding-left: $base-padding;
  overflow: hidden;
  user-select: none;
  background: $base-color-white;
  box-shadow: $base-box-shadow;

  .left-panel {
    display: flex;
    align-items: center;
    justify-items: center;
    height: 60px;

    .fold-unfold {
      font-size: 18px;
      color: $base-color-gray;
      cursor: pointer;
    }
  }

  .right-panel {
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: flex-end;
    height: $base-nav-bar-height;
  }
}
</style>
