import http from "@/utils/http";
import type {
  loginQueryInterface,
  SmsCodeQueryInterface,
  UserInfoInterface
} from "@/api/user/types";

/**
 * @description 登录
 * @param params
 */
export function login(params: loginQueryInterface) {
  return http.post<string>(`/app/login`, params);
}

/**
 * @description 获取短信验证码
 * @param params
 */
export function getSmsCode(params: SmsCodeQueryInterface) {
  return http.get(`/app/login/getCode`, params);
}

/**
 * @description 获取用户信息
 */
export function getUserInfo() {
  return http.get<UserInfoInterface>(`/app/info`);
}
