import { RepairStatus } from '@/enums/constEnums'

// 报修信息
export interface RepairInfoInterface {
  id: number | string
  userId: number | string
  agreementId: number | string
  apartmentId: number | string
  roomId: number | string
  repairContent: string
  status: RepairStatus
  createTimeStr: string
  apartmentName: string
  roomNumber: string
  userName: string
  userPhone: string
}

// 报修查询参数
export interface RepairListQueryInterface {
  pageNum: number
  pageSize: number
  name?: string
  phone?: string
}
