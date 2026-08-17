import {RouteRecord, RouteRecordRaw} from 'vue-router'
export function getShowMenuList(routeList: RouteRecordRaw[]) {
  let newMenuList: Menu.MenuOptions[] = JSON.parse(JSON.stringify(routeList))
  return newMenuList.filter((item) => {
    item.children?.length &&
      (item.children = getShowMenuList(item.children as RouteRecord[]))
    return !item.meta?.isHide
  })
}
