import type { NotificationType } from "@/enums/constEnums";

// 通知列表
export interface NotificationItemInterface {
  id: number | string;
  userId: number | string;
  type: NotificationType;
  title: string;
  content: string;
  bizType: string;
  bizId: number | string;
  isRead: boolean;
  createTimeStr: string;
}
