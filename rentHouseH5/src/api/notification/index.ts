import http from "@/utils/http";
import type { NotificationItemInterface } from "@/api/notification/types";

/**
 * @description 查询我的通知列表
 */
export function getNotificationList() {
  return http.get<NotificationItemInterface[]>(`/app/notification/listItem`);
}

/**
 * @description 根据id标记通知已读
 * @param id
 */
export function readNotificationById(id: number | string) {
  return http.post(`/app/notification/readById?id=${id}`);
}
