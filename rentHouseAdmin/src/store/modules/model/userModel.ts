import type { UserInfo } from '@/api/user/types'

export interface UserState {
  userInfo: UserInfo
  token?: string
}
