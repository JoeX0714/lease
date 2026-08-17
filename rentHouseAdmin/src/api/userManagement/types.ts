import { UserStatus } from '@/enums/constEnums'
import { ReqPage } from '@/api/types'
// 分页查询用户管理信息
export interface UserManagementListQueryInterface extends ReqPage {
  // 	用户手机号码
  phone?: number | string
  status?: UserStatus
}
// 用户管理信息
export interface UserManagementInfoInterface {
  id: number | string
  phone: string
  password: string
  avatarUrl: string
  nickname: string
  status: UserStatus
}
