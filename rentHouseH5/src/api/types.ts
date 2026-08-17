// * 分页请求参数
export interface ReqPage {
  current: number;
  size: number;
}

// * 分页响应参数
export interface PageRes<T> {
  records: T[];
  // 当前页
  current?: number;
  // 每页显示条数
  size?: number;
  total: number;
}
