import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { staticRoutes } from './constantRoutes'
import * as NProgress from 'nprogress'
import { useUserStore } from '@/store/modules/user'
import { LOGIN_URL, ROUTER_WHITE_LIST } from '@/config/config'

const router = createRouter({
  history: createWebHashHistory(),
  routes: staticRoutes as RouteRecordRaw[],
  // 刷新时，滚动条位置还原
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const userStore = useUserStore()
  // 1.判断是访问登陆页，有 Token 就在当前页面，没有 Token 重置路由并放行到登陆页
  if (to.path === LOGIN_URL) {
    if (userStore.token) return next(from.fullPath)
    return next()
  }

  // 2.判断访问页面是否在路由白名单(不需要登陆)地址中，如果存在直接放行
  if (ROUTER_WHITE_LIST.includes(to.path)) return next()

  // 3.判断是否有 Token，没有token跳转到登陆页面并且携带原目标路径
  if (!userStore.token) {
    return next({ path: LOGIN_URL, query: { redirect: to.fullPath } })
  }

  // 4.上述条件都不满足，直接放行
  return next()
})

/**
 * @description 路由跳转结束
 * */
router.afterEach(() => {
  NProgress.done()
})

/**
 * @description 路由跳转错误
 * */
router.onError((error) => {
  NProgress.done()
  console.warn('路由错误', error.message)
})

export default router
