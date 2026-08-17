// 数据字典接口
export interface DictionaryInterfaceRes {
  id: number
  createTime: string
  updateTime: string
  isDeleted: number
  param: object
  parentId: number
  name: string
  value: string
  dictCode: string
  hasChildren: boolean
}
