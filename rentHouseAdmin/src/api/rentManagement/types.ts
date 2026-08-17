import {
  AgreementSource,
  AgreementStatus,
  AppointmentStatus,
  RoomReleaseStatus,
} from '@/enums/constEnums'
import {
  ApartmentInterface,
  TermInfoInterface,
} from '@/api/apartmentManagement/types'
import { ReqPage } from '@/api/types'
//#region <预约看房>
// 预约信息
export interface AppointmentInfoInterface {
  id: number | string
  userId: number | string
  name: string
  phone: string
  apartmentId: number | string
  appointmentTime: string
  additionalInfo: string
  appointmentStatus: AppointmentStatus
  apartmentInfo: Omit<ApartmentInterface, 'totalRoomCount' | 'freeRoomCount'>
}
export interface AppointmentInfoListQueryInterface extends ReqPage {
  // 省份id
  provinceId?: number | string
  // 城市id
  cityId?: number | string
  // 区域id
  districtId?: number | string
  // 公寓id
  apartmentId?: number | string
  // 预约用户姓名
  name?: string
  // 预约用户手机号码
  phone?: string
}
//#endregion
//#region <租约管理>
export interface AgreementInfoListQueryInterface extends ReqPage {
  // 省份id
  provinceId?: number | string
  // 城市id
  cityId?: number | string
  // 区域id
  districtId?: number | string
  // 公寓id
  apartmentId?: number | string
  // 房间 号
  roomNumber?: number | string
  // 预约用户姓名
  name?: string
  // 预约用户手机号码
  phone?: string
}
// 租约信息
export interface AgreementInfoInterface extends AgreementInterface {
  apartmentInfo: Omit<ApartmentInterface, 'totalRoomCount' | 'freeRoomCount'>
  roomInfo: RoomInfoInterface
  paymentTypeInfo: PaymentTypeInterface
  leaseTerm: LeaseTermInterface
}
export interface AgreementInterface {
  id: number | string
  phone: string
  name: string
  identificationNumber: string
  apartmentId: number | string
  roomId: number | string
  leaseStartDate: string
  leaseEndDate: string
  leaseTermId: number | string
  rent: number | string
  deposit: number | string
  paymentTypeId: number | string
  status: AgreementStatus
  sourceType: AgreementSource
  additionalInfo: string
}
export interface RoomInfoInterface {
  id: number | string
  roomNumber: number | string
  rent: number | string
  apartmentId: number | string
  isRelease: RoomReleaseStatus
}
export interface PaymentTypeInterface {
  id: number | string
  name: string
  payMonthCount: number | string
  additionalInfo: string
}
export type LeaseTermInterface = TermInfoInterface
//#endregion
