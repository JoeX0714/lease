/* 登录接口参数类型 */
export interface LoginData {
  username: string
  password: string
  captchaKey: string
  captchaCode: string
}

export interface ValidateUCodeData {
  image: string
  key: string
}

/* 用户信息接口返回值类型 */
export interface UserInfo {
  name: string
  avatarUrl: string
}
