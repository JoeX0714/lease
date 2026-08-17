// 分页查询公寓信息列表
import { ReqPage } from '@/api/types'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'
//#region <公寓>
export interface ApartmentListQueryInterface extends ReqPage {
  // 省份id
  provinceId?: number | string
  // 城市id
  cityId?: number | string
  // 区域id
  districtId?: number | string
}
export interface ApartmentInterface {
  id: number | string
  name: string
  introduction: string
  districtId: number | string
  districtName: string
  cityId: number | string
  cityName: string
  provinceId: number | string
  provinceName: string
  addressDetail: string
  latitude: number | string
  longitude: number | string
  phone: string
  isRelease: string | number
  totalRoomCount: number
  freeRoomCount: number
  // 	公寓配套id
  facilityInfoIds?: number[]
  // 	公寓配套信息
  facilityInfoList?: FacilityInfoInterface[]
  // 	公寓标签id
  labelIds?: number[]
  // 公寓标签信息
  labelInfoList?: LabelInfoInterface[]
  // 	公寓杂费值id
  feeValueIds?: number[]
  // 	公寓杂费值信息
  feeValueVoList?: FeeInfoInterface['feeValueList']
  // 图片信息
  graphVoList?:
    | {
        url: string
        name: number
      }[]
    | UploadFile[]
}
// 省市区
export interface RegionInterface {
  id: number
  name: string
}
export interface AddressOptionsInterface {
  label: string
  value: string
  location?: { lng: number | string; lat: number | string }
}
// 配套信息
export interface FacilityInfoInterface {
  id: number | string
  type: string
  name: string
  icon: string | null
}
// 标签信息
export interface LabelInfoInterface {
  id: number | string
  type: string
  name: string
}
// 杂费值信息
export interface FeeInfoInterface {
  id: number | string
  name: string
  feeValueList: FeeValueInfoInterface[]
}
export interface FeeValueInfoInterface {
  id: number | string
  name: string
  unit: string
  feeKeyId: number | string
  feeKeyName?: number | string
}
// 树形结构
export interface TreeData {
  id?: number | string
  value?: number | string
  label: string
  parentId?: number | string
  children?: TreeData[]
}
//#endregion
//#region <房间>
// 房间请求参数
export interface RoomListQueryInterface extends ReqPage {
  // 省份id
  provinceId?: number | string
  // 城市id
  cityId?: number | string
  // 区域id
  districtId?: number | string
  // 公寓id
  apartmentId?: number | string
}
// 房间的信息
export interface RoomInterface {
  id: number | string
  roomNumber: number | string
  rent: number | string
  apartmentId: number | string
  isRelease: string | number
  leaseEndDate: string
  isCheckIn: boolean
  apartmentInfo: Omit<ApartmentInterface, 'totalRoomCount' | 'freeRoomCount'>
  // 	属性信息列表
  attrValueVoList?: any[]
  // 	公寓配套信息
  facilityInfoList?: FacilityInfoInterface[]
  // 公寓标签信息
  labelInfoList?: LabelInfoInterface[]
  // 	支付方式列表
  paymentTypeList?: PaymentInfoInterface[]
  // 可选租期列表
  leaseTermList?: TermInfoInterface[]
  // 图片信息
  graphVoList?:
    | {
        url: string
        name: number
      }[]
    | UploadFile[]
}
// 保存房间的信息
export interface SaveRoomInterface {
  id: number | string
  roomNumber: string
  rent: number | string
  apartmentId: number | string
  isRelease: string | number
  // 	属性信息列表
  attrValueIds: number[]
  // 	配套id
  facilityInfoIds: number[]
  // 	标签信息列表
  labelInfoIds: number[]
  // 	支付方式列表
  paymentTypeIds: number[]
  // 可选租期列表
  leaseTermIds: number[]
  // 图片信息
  graphVoList:
    | {
        url: string
        name: number
      }[]
    | UploadFile[]
}
// 属性信息列表
export interface AttrInfoInfoInterface {
  id: number | string
  name: string
  attrValueList: AttrValueInfoInterface[]
}
export interface AttrValueInfoInterface {
  id: number | string
  name: string
  attrKeyId: number | string
  attrKeyName?: string
}
// 支付方式信息列表
export interface PaymentInfoInterface {
  id: number | string
  name: string
  payMonthCount: string | number
  additionalInfo: string
}
// 租期信息列表
export interface TermInfoInterface {
  id: number | string
  unit: string
  monthCount: string
}
//#endregion
//#region <属性>
//#endregion
