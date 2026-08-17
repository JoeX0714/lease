/*
 * @Author: 朽木白
 * @Date: 2023-02-06 11:02:58
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-10 15:22:05
 * @Description: 用户store
 */
import { defineStore } from 'pinia'
import { getUserInfo } from '@/api/user'
import type { UserState } from './model/userModel'
import type { UserInfo } from '@/api/user/types'
import { RESEETSTORE } from '@/utils/reset'

export const useUserStore = defineStore({
  id: 'app-user',
  state: (): UserState => ({
    token: '',
    userInfo: {} as UserInfo,
  }),
  actions: {
    // setToken
    setToken(token: string) {
      this.token = token
    },
    // setUserInfo
    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
    },
    async Logout() {
      // await logout()
      RESEETSTORE()
    },
  },
  // 设置为true，缓存state
  persist: true,
})
