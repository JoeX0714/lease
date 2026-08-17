import http from '@/utils/http'
import {
  ApartmentInterface,
  ApartmentListQueryInterface,
  AttrInfoInfoInterface,
  AttrValueInfoInterface,
  FacilityInfoInterface,
  FeeInfoInterface,
  FeeValueInfoInterface,
  LabelInfoInterface,
  PaymentInfoInterface,
  RegionInterface,
  RoomInterface,
  RoomListQueryInterface,
  SaveRoomInterface,
  TermInfoInterface,
} from '@/api/apartmentManagement/types'
import { PageRes } from '@/api/types'
import {
  ApartmentReleaseStatus,
  BuildingType,
  RoomReleaseStatus,
} from '@/enums/constEnums'
//#region <公寓>
/**
 * @description 分页查询公寓信息列表
 * @param params
 */
export function getApartmentList(params: ApartmentListQueryInterface) {
  return http.get<PageRes<ApartmentInterface[]>>(`/admin/apartment/pageItem`, {
    current: params.pageNum,
    size: params.pageSize,
    provinceId: params.provinceId,
    cityId: params.cityId,
    districtId: params.districtId,
  })
}
/**
 * @description 查询省份列表
 */
export function getProvinceList() {
  return http.get<RegionInterface[]>(`/admin/region/province/list`)
}
/**
 * @description 查询城市列表
 * @param provinceId
 */
export function getCityList(provinceId: number | string) {
  return http.get<RegionInterface[]>(
    `/admin/region/city/listByProvinceId?id=${provinceId}`,
  )
}
/**
 * @description 查询区域列表
 * @param cityId
 */
export function getDistrictList(cityId: number | string) {
  return http.get<RegionInterface[]>(
    `/admin/region/district/listByCityId?id=${cityId}`,
  )
}
/**
 * @description 根据id修改公寓的发布状态
 * @param id
 * @param status
 */
export function updateApartmentReleaseStatus(
  id: number | string,
  status: ApartmentReleaseStatus,
) {
  return http.post(
    `/admin/apartment/updateReleaseStatusById?id=${id}&status=${status}`,
  )
}
/**
 * @description 保存或更新公寓信息
 * @param params
 */
export function saveOrUpdateApartment(params: Partial<ApartmentInterface>) {
  return http.post(`/admin/apartment/saveOrUpdate`, params)
}
/**
 * @description 根据id删除公寓
 * @param id
 */
export function deleteApartmentById(id: number | string) {
  return http.delete(`/admin/apartment/removeById?id=${id}`)
}
/**
 * @description 查询配套信息列表
 * @param type
 */
export function getFacilityInfoList(type: BuildingType | null = null) {
  return http.get<FacilityInfoInterface[]>(
    `/admin/facility/list${type ? `?type=${type}` : ''}`,
  )
}
/**
 * @description 查询标签信息列表
 */
export function getLabelInfoList(type: BuildingType | null = null) {
  return http.get<LabelInfoInterface[]>(
    `/admin/label/list${type ? `?type=${type}` : ''}`,
  )
}
/**
 * @description 查询杂费信息列表
 */
export function getFeeInfoList() {
  return http.get<FeeInfoInterface[]>(`/admin/fee/list`)
}
/**
 * @description 根据id获取公寓信息
 */
export function getApartmentById(id: number | string) {
  return http.get<ApartmentInterface>(`/admin/apartment/getDetailById?id=${id}`)
}
//#endregion
//#region <房间>
/**
 * @description 分页查询房间的信息列表
 * @param params
 */
export function getRoomList(params: RoomListQueryInterface) {
  return http.get<PageRes<RoomInterface[]>>(`/admin/room/pageItem`, {
    current: params.pageNum,
    size: params.pageSize,
    provinceId: params.provinceId,
    cityId: params.cityId,
    districtId: params.districtId,
    apartmentId: params.apartmentId,
  })
}
/**
 * @description 根据区县id查询公寓信息列表
 * @param districtId
 */
export function getApartmentListByDistrictId(districtId: number | string) {
  return http.get<ApartmentInterface[]>(
    `/admin/apartment/listInfoByDistrictId?id=${districtId}`,
  )
}
/**
 * @description 根据id修改房间发布状态
 * @param id
 * @param status
 */
export function updateRoomReleaseStatus(
  id: number | string,
  status: RoomReleaseStatus,
) {
  return http.post(
    `/admin/room/updateReleaseStatusById?id=${id}&status=${status}`,
  )
}
/**
 * @description 根据id删除房间信息
 * @param id
 */
export function deleteRoomById(id: number | string) {
  return http.delete(`/admin/room/removeById?id=${id}`)
}
/**
 * @description 根据id获取房间信息
 * @param id
 */
export function getRoomById(id: number | string) {
  return http.get<RoomInterface>(`/admin/room/getDetailById?id=${id}`)
}
/**
 * @description 保存或更新房间的信息
 */
export function saveOrUpdateRoom(params: SaveRoomInterface) {
  return http.post(`/admin/room/saveOrUpdate`, params)
}
/**
 * @description 查询属性信息列表
 */
export function getAttrInfoList() {
  return http.get<AttrInfoInfoInterface[]>(`/admin/attr/list`)
}
/**
 * @description 查询支付方式列表
 */
export function getPaymentList() {
  return http.get<PaymentInfoInterface[]>(`/admin/payment/list`)
}

/**
 * @description 保存或更新支付方式
 */
export function saveOrUpdatePayment(params: PaymentInfoInterface) {
  return http.post(`/admin/payment/saveOrUpdate`, params)
}

/**
 * @description 根据id删除支付方式
 * @param id
 */
export function deletePaymentById(id: number | string) {
  return http.delete(`/admin/payment/deleteById?id=${id}`)
}
/**
 * @description 查询租期列表
 */
export function getTermList() {
  return http.get<TermInfoInterface[]>(`/admin/term/list`)
}

/**
 * @description 保存或更新租期信息
 */
export function saveOrUpdateTerm(params: TermInfoInterface) {
  return http.post(`/admin/term/saveOrUpdate`, params)
}

/**
 * @description 根据id删除租期信息
 */
export function deleteTermById(id: number | string) {
  return http.delete(`/admin/term/deleteById?id=${id}`)
}
//#endregion
//#region <属性>
/**
 * @description 新增或修改配套信息
 * @param params
 */
export function saveOrUpdateFacilityInfo(
  params: FacilityInfoInterface & { id?: number | string },
) {
  return http.post(`/admin/facility/saveOrUpdate`, params)
}
/**
 * @description 根据id删除配套信息
 * @param id
 */
export function deleteFacilityInfoById(id: number | string) {
  return http.delete(`/admin/facility/deleteById?id=${id}`)
}
/**
 * @description 新增或修改标签信息
 * @param params
 */
export function saveOrUpdateLabelInfo(
  params: LabelInfoInterface & { id?: number | string },
) {
  return http.post(`/admin/label/saveOrUpdate`, params)
}
/**
 * @description 根据id删除标签信息
 * @param id
 */
export function deleteLabelInfoById(id: number | string) {
  return http.delete(`/admin/label/deleteById?id=${id}`)
}
/**
 * @description 新增或更新属性Value
 * @param params
 */
export function saveOrUpdateAttrValue(
  params: AttrValueInfoInterface & { id?: number | string },
) {
  return http.post(`/admin/attr/value/saveOrUpdate`, params)
}
/**
 * @description 根据id删除属性Value
 * @param id
 */
export function deleteAttrValueById(id: number | string) {
  return http.delete(`/admin/attr/value/deleteById?id=${id}`)
}
/**
 * @description 新增或更新属性Key
 * @param params
 */
export function saveOrUpdateAttrKey(
  params: Pick<AttrValueInfoInterface, 'name' | 'id'> & {
    id?: number | string
  },
) {
  return http.post(`/admin/attr/key/saveOrUpdate`, params)
}
/**
 * @description 根据id删除属性Key
 * @param id
 */
export function deleteAttrKeyById(id: number | string) {
  return http.delete(`/admin/attr/key/deleteById?attrKeyId=${id}`)
}

/**
 * @description 新增或更新杂费Value
 * @param params
 */
export function saveOrUpdateFeeValue(
  params: FeeValueInfoInterface & { id?: number | string },
) {
  return http.post(`/admin/fee/value/saveOrUpdate`, params)
}
/**
 * @description 根据id删除杂费Value
 * @param id
 */
export function deleteFeeValueById(id: number | string) {
  return http.delete(`/admin/fee/value/deleteById?id=${id}`)
}
/**
 * @description 新增或更新杂费Key
 * @param params
 */
export function saveOrUpdateFeeKey(
  params: Pick<FeeValueInfoInterface, 'name' | 'id'> & {
    id?: number | string
  },
) {
  return http.post(`/admin/fee/key/saveOrUpdate`, params)
}
/**
 * @description 根据id删除杂费Key
 * @param id
 */
export function deleteFeeKeyById(id: number | string) {
  return http.delete(`/admin/fee/key/deleteById?feeKeyId=${id}`)
}
//#endregion
