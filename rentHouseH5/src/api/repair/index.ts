import http from "@/utils/http";
import type {
  RepairItemInterface,
  RepairSubmitInterface
} from "@/api/repair/types";

/**
 * @description 提交报修
 * @param params
 */
export function submitRepair(params: RepairSubmitInterface) {
  return http.post(`/app/repair/save`, params);
}

/**
 * @description 查询我的报修列表
 */
export function getMyRepairList() {
  return http.get<RepairItemInterface[]>(`/app/repair/listItem`);
}
