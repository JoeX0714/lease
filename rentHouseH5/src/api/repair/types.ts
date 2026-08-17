import type { RepairStatus } from "@/enums/constEnums";

// 报修提交参数
export interface RepairSubmitInterface {
  agreementId: number | string;
  roomId: number | string;
  repairContent: string;
}

// 报修列表
export interface RepairItemInterface {
  id: number | string;
  userId: number | string;
  agreementId: number | string;
  apartmentId: number | string;
  roomId: number | string;
  repairContent: string;
  status: RepairStatus;
  apartmentName: string;
  roomNumber: string;
  createTimeStr: string;
}
