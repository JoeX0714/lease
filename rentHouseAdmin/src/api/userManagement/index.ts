import http from '@/utils/http'
import {
  UserManagementInfoInterface,
  UserManagementListQueryInterface,
} from '@/api/userManagement/types'
import { PageRes } from '@/api/types'
import { UserStatus } from '@/enums/constEnums'
/**
 * @description 分页查询用户管理信息
 * @param params
 */
export function getUserManagementInfoList(
  params: UserManagementListQueryInterface,
) {
  return http.get<PageRes<UserManagementInfoInterface[]>>(`/admin/user/page`, {
    current: params.pageNum,
    size: params.pageSize,
    phone: params.phone,
    status: params.status,
  })
}
/**
 * @description 根据用户id更新账号状态
 * @param id
 * @param status
 */
export function updateUserManagementStatus(
  id: number | string,
  status: UserStatus,
) {
  return http.post(`/admin/user/updateStatusById?id=${id}&status=${status}`)
}
