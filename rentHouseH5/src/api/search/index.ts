import http from "@/utils/http";
import type {
  AgreementDetailInterface,
  AgreementItemInterface,
  AgreementQueryInterface,
  ApartmentInterface,
  AppointmentInfoInterface,
  AppointmentItemInterface,
  AppointmentQueryInterface,
  HistoryItemInterface,
  PaymentInfoInterface,
  RegionInterface,
  RoomDetailInterface,
  RoomInterface,
  RoomListQueryInterface,
  TermInfoInterface
} from "@/api/search/types";
import type { PageRes, ReqPage } from "@/api/types";
/**
 * @description 分页查询房间列表
 * @param params
 */
export function getRoomList(params: Partial<RoomListQueryInterface>) {
  return http.get<PageRes<RoomInterface>>(`/app/room/pageItem`, params);
}
/**
 * @description 查询省市区列表
 */
export function getRegionList() {
  return http.get<RegionInterface[]>(`/app/region/listAsTree`);
}
/**
 * @description 查询省份列表
 */
export function getProvinceList() {
  return http.get<RegionInterface[]>(`/app/region/province/list`);
}
/**
 * @description 查询城市列表
 * @param provinceId
 */
export function getCityList(provinceId: number | string) {
  return http.get<RegionInterface[]>(
    `/app/region/city/listByProvinceId?id=${provinceId}`
  );
}
/**
 * @description 查询区域列表
 * @param cityId
 */
export function getDistrictList(cityId: number | string) {
  return http.get<RegionInterface[]>(
    `/app/region/district/listByCityId?id=${cityId}`
  );
}
/**
 * @description 获取支付方式列表
 */
export function getPaymentTypeList() {
  return http.get<PaymentInfoInterface[]>(`/app/payment/list`);
}
/**
 * @description 根据id获取房间的详细信息
 * @param id
 */
export function getRoomDetailById(id: number | string) {
  return http.get<RoomDetailInterface>(`/app/room/getDetailById?id=${id}`);
}

/**
 * @description 根据id获取公寓信息
 */
export function getApartmentDetailById(id: number | string) {
  return http.get<ApartmentInterface>(`/app/apartment/getDetailById?id=${id}`);
}

/**
 * @description 根据公寓id分页查询房间列表
 * @param params
 */
export function getRoomListByApartmentId(
  params: ReqPage & { id: number | string }
) {
  return http.get<PageRes<RoomInterface>>(
    `/app/room/pageItemByApartmentId`,
    params
  );
}

/**
 * @description 保存或更新预约
 */
export function saveOrUpdateAppointment(params: AppointmentQueryInterface) {
  return http.post(`/app/appointment/saveOrUpdate`, params);
}

/**
 * @description 根据id查询预约详情信息
 * @param id
 */
export function getAppointmentDetailById(id: number | string) {
  return http.get<AppointmentInfoInterface>(
    `/app/appointment/getDetailById?id=${id}`
  );
}

/**
 * @description 查询我的预约列表
 */
export function getMyAppointmentList() {
  return http.get<AppointmentItemInterface[]>(`/app/appointment/listItem`);
}

/**
 * @description 获取个人租约基本信息列表
 */
export function getMyAgreementList() {
  return http.get<AgreementItemInterface[]>(`/app/agreement/listItem`);
}

/**
 * @description 根据id获取租约详细信息
 */
export function getAgreementDetailById(id: number | string) {
  return http.get<AgreementDetailInterface>(
    `/app/agreement/getDetailById?id=${id}`
  );
}

/**
 * @description 保存新租约
 */
export function saveOrUpdateAgreement(
  params: Partial<AgreementQueryInterface>
) {
  return http.post(`/app/agreement/saveOrUpdate`, params);
}

/**
 * @description 根据房间id获取可选获取租期列表
 * @param roomId
 */
export function getTermListByRoomId(roomId: number | string) {
  return http.get<TermInfoInterface[]>(`/app/term/listByRoomId?id=${roomId}`);
}
/**
 * @description 根据房间id获取可选支付方式列表
 * @param roomId
 */
export function getPaymentListByRoomId(roomId: number | string) {
  return http.get<PaymentInfoInterface[]>(
    `/app/payment/listByRoomId?id=${roomId}`
  );
}

/**
 * @description 获取浏览历史
 * @param params
 */
export function getHistoryList(params: ReqPage) {
  return http.get<PageRes<HistoryItemInterface>>(
    `/app/history/pageItem`,
    params
  );
}
