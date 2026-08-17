import http from '@/utils/http'
import {
  RepairInfoInterface,
  RepairListQueryInterface,
} from '@/api/repairManagement/types'
import { PageRes } from '@/api/types'
import { RepairStatus } from '@/enums/constEnums'

/**
 * @description 分页查询报修信息
 * @param params
 */
export function getRepairInfoList(params: RepairListQueryInterface) {
  return http.get<PageRes<RepairInfoInterface[]>>(`/admin/repair/page`, {
    current: params.pageNum,
    size: params.pageSize,
    name: params.name,
    phone: params.phone,
  })
}

/**
 * @description 根据id修改报修状态
 * @param id
 * @param status
 */
export function updateRepairStatusById({
  id,
  status,
}: {
  id: number | string
  status: RepairStatus
}) {
  return http.post(`/admin/repair/updateStatusById?id=${id}&status=${status}`)
}
