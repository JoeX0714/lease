/*
 * @Author: 朽木白
 * @Date: 2023-02-27 09:50:34
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-10 14:16:24
 * @Description: 系统用户登陆信息接口
 */
import http from '@/utils/http'
// import http from '@/utils/http/httpMock'
import type { LoginData, UserInfo } from './types'
import { ValidateUCodeData } from './types'

/**
 * 登录
 */
export function login(data: LoginData) {
  return http.post<string>('/admin/login', data)
}
/**
 * 获取验证码
 */
export function getCode() {
  return http.get<ValidateUCodeData>('/admin/login/captcha')
}
/**
 * 获取登录用户信息
 */
export function getUserInfo() {
  return http.get<UserInfo>('/admin/info')
}

/**
 * 退出登陆
 */
export function logout() {
  return http.post('/admin/system/index/logout')
}
