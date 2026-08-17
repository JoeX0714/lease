<template>
  <el-breadcrumb class="app-breadcrumb" separator-icon="ArrowRight">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in matched" :key="item.path">
        <el-icon size="14">
          <component :is="item.meta?.icon"></component>
        </el-icon>
        <span
          v-if="item.redirect === 'noRedirect' || index == matched.length - 1"
          class="no-redirect"
        >
          {{ item.meta?.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta?.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useRoute, useRouter, RouteRecordRaw } from 'vue-router'
import { computed } from 'vue'

export default defineComponent({
  setup() {
    const route = useRoute()
    const router = useRouter()
    const handleLink = (item: RouteRecordRaw) => {
      router.push({
        path: item.path,
      })
    }

    const matched = computed(() =>
      route.matched.filter(
        (item) =>
          item.meta &&
          item.meta.title &&
          item.meta.breadcrumb !== false &&
          item.children.length !== 1,
      ),
    )

    return {
      handleLink,
      matched,
    }
  },
})
</script>

<style scoped lang="scss">
.app-breadcrumb {
  margin-left: 20px;
}

:deep(.el-breadcrumb__inner) {
  display: flex;

  > i {
    margin-right: 3px;
  }
}
</style>
