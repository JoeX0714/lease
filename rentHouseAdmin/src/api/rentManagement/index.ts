import http from '@/utils/http'
import {
  AgreementInfoInterface,
  AgreementInfoListQueryInterface,
  AgreementInterface,
  AppointmentInfoInterface,
  AppointmentInfoListQueryInterface,
  RoomInfoInterface,
} from '@/api/rentManagement/types'
import { PageRes } from '@/api/types'
import { AgreementStatus, AppointmentStatus } from '@/enums/constEnums'
//#region <预约看房>
/**
 * @description 分页查询预约信息
 * @param params
 */
export function getAppointmentInfoList(
  params: AppointmentInfoListQueryInterface,
) {
  return http.get<PageRes<AppointmentInfoInterface[]>>(
    `/admin/appointment/page`,
    {
      current: params.pageNum,
      size: params.pageSize,
      provinceId: params.provinceId,
      cityId: params.cityId,
      districtId: params.districtId,
      apartmentId: params.apartmentId,
      name: params.name,
      phone: params.phone,
    },
  )
}
/**
 * @description 根据id更新预约状态
 * @param id
 * @param status
 */
export function updateAppointmentStatusById(
  id: number | string,
  status: AppointmentStatus,
) {
  return http.post(
    `/admin/appointment/updateStatusById?id=${id}&status=${status}`,
  )
}
//#endregion
//#region <租赁管理>
/**
 * @description 分页查询租约信息
 * @param params
 */
export function getAgreementInfoList(params: AgreementInfoListQueryInterface) {
  return http.get<PageRes<AgreementInfoInterface[]>>(`/admin/agreement/page`, {
    current: params.pageNum,
    size: params.pageSize,
    provinceId: params.provinceId,
    cityId: params.cityId,
    districtId: params.districtId,
    apartmentId: params.apartmentId,
    roomNumber: params.roomNumber,
    name: params.name,
    phone: params.phone,
  })
}
/**
 * @description 根据公寓id查询房间列表
 * @param apartmentId
 */
export function getRoomListByApartmentId(apartmentId: number | string) {
  return http.get<RoomInfoInterface[]>(
    `/admin/room/listBasicByApartmentId?id=${apartmentId}`,
  )
}
/**
 * @description 根据id查询租约信息
 * @param id
 */
export function getAgreementInfoById(id: number | string) {
  return http.get<AgreementInfoInterface>(`/admin/agreement?id=${id}`)
}
/**
 * @description 保存或修改租约信息
 * @param data
 */
export function saveOrUpdateAgreementInfo(data: AgreementInterface) {
  return http.post(`/admin/agreement/saveOrUpdate`, data)
}
/**
 * @description 根据id删除租约信息
 * @param id
 */
export function deleteAgreementInfoById(id: number | string) {
  return http.delete(`/admin/agreement/removeById?id=${id}`)
}
/**
 * @description 根据id更新租约状态
 * @param id
 * @param status
 */
export function updateAgreementStatusById({
  id,
  status,
}: {
  id: number | string
  status: AgreementStatus
}) {
  return http.post(
    `/admin/agreement/updateStatusById?id=${id}&status=${status}`,
  )
}
//#endregion
