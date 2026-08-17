// * 分页请求参数
export interface ReqPage {
  pageNum: number
  pageSize: number
}

// * 分页响应参数
export interface PageRes<T> {
  records: T[]
  // 当前页
  current?: number
  // 每页显示条数
  size?: number
  total: number
}
