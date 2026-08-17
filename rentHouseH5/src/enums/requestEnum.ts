/**
 * @description: 响应结果
 * @argument SUCCESS  请求成功
 * @argument EXPIRE   token请求失效或校验失败
 * @argument ERROR    请求错误
 * @argument TIMEOUT  请求超时
 * @argument TYPE     请求类型
 */
// export enum ResultEnum {
//   SUCCESS = 200,
//   EXPIRE = 203,
//   ERROR = -1,
//   ERRMESSAGE = "请求失败",
//   TIMEOUT = 25000,
//   TYPE = "success"
// }
export const ResultEnum = {
  SUCCESS: 200,
  EXPIRE: [305, 501, 601, 602],
  ERROR: -1,
  ERRMESSAGE: "请求失败",
  TIMEOUT: 25000,
  TYPE: "success"
} as const;
