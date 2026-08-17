import { defineStore } from "pinia";
import type {
  loginQueryInterface,
  UserInfoInterface,
  UserStateInterface
} from "@/api/user/types";
import { getUserInfo, login } from "@/api/user";
import { removeToken, setToken } from "@/utils/token";

export const useUserStore = defineStore({
  id: "app-user",
  state: (): UserStateInterface => ({
    token: null,
    userInfo: null
  }),
  actions: {
    // setToken
    setToken(token: string) {
      this.token = token;
    },
    // login
    async LoginAction(params: loginQueryInterface) {
      const { data } = await login(params);
      setToken(data);
      await this.GetInfoAction();
    },
    // setUserInfo
    setUserInfo(userInfo: UserInfoInterface) {
      this.userInfo = userInfo;
    },
    async GetInfoAction() {
      const token = this.token;
      if (!token) {
        this.userInfo = null;
        return;
      }
      const { data } = await getUserInfo();
      if (this.token === token) {
        // 存储用户信息
        this.setUserInfo(data);
      }
    },
    async Logout() {
      // await logout()
      this.resetUserStore();
      removeToken();
    },
    resetUserStore() {
      this.token = null;
      this.userInfo = null;
    }
  },
  // 设置为true，缓存state
  persist: true
});
