/*
 * @Description:
 * @Autor: codeBo
 * @Date: 2023-03-03 15:37:08
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-09 10:01:43
 */
declare type Nullable<T> = T | null
declare type NonNullable<T> = T extends null | undefined ? never : T
declare type Recordable<T = any> = Record<string, T>
declare module 'path-browserify'
// * Vite
declare interface ViteEnv {
  NODE_ENV: string
  VITE_APP_PORT: number
  VITE_APP_TITLE: string
  VITE_APP_BASE_API: string
}

/**
 * @description 动态路由参数配置简介 📚
 * @param path ==> 菜单路径
 * @param name ==> 菜单别名
 * @param redirect ==> 重定向地址
 * @param component ==> 视图文件路径
 * @param meta ==> 菜单信息
 * @param meta.icon ==> 菜单图标
 * @param meta.title ==> 菜单标题
 * @param meta.activeMenu ==> 当前路由为详情页时，需要高亮的菜单
 * @param meta.isLink ==> 是否外链
 * @param meta.isHide ==> 是否隐藏
 * @param meta.isFull ==> 是否全屏(示例：数据大屏页面)
 * @param meta.isAffix ==> 是否固定在 tabs nav
 * @param meta.isKeepAlive ==> 是否缓存
 * @param meta.noAuth ===> 是否校验权限
 * */

// * Menu
declare namespace Menu {
  interface MenuOptions {
    path: string
    name: string
    component?: string | (() => Promise<any>)
    redirect?: string
    meta: MetaProps
    children?: MenuOptions[]
  }
  interface MetaProps {
    icon?: string
    name?: string
    activeMenu?: string
    isLink?: string
    isHide?: boolean
    isFull?: boolean
    isAffix?: boolean
    isKeepAlive?: boolean
  }
}
