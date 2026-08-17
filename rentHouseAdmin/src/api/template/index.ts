import http from '@/utils/http'
import { DictionaryInterfaceRes } from '@/api/template/types'
/**
 * @description 获取数据字典接口
 * @param params
 */
export function getDictionary(params: { id: number }) {
  return http.get<DictionaryInterfaceRes[]>(
    `/admin/cmn/dict/findByParentId/${params.id}`,
  )
}
