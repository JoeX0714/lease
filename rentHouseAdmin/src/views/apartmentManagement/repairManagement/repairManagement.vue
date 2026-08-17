<template>
  <el-card>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getRepairInfoList"
      :initParam="initParam"
      :stripe="true"
    >
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          type="danger"
          :disabled="scope.row.status !== RepairStatus.PENDING"
          @click="cancelRepairHandle(scope.row)"
        >
          撤销
        </el-button>
        <el-button
          type="success"
          :disabled="scope.row.status !== RepairStatus.PENDING"
          @click="completeRepairHandle(scope.row)"
        >
          办结
        </el-button>
      </template>
    </ProTable>

    <el-dialog v-model="contentDialogVisible" title="报修信息" width="500px">
      <div>{{ currentContent }}</div>
    </el-dialog>
  </el-card>
</template>

<script setup lang="tsx">
import { reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import ProTable from '@/components/ProTable/src/ProTable.vue'
import {
  getRepairInfoList,
  updateRepairStatusById,
} from '@/api/repairManagement'
import { RepairInfoInterface } from '@/api/repairManagement/types'
import {
  getLabelByValue,
  RepairStatus,
  RepairStatusMap,
} from '@/enums/constEnums'
import { useHandleData } from '@/hooks/useHandleData'

// *获取 ProTable 元素，调用其获取刷新数据方法
const proTable = ref<InstanceType<typeof ProTable>>()

// *表格配置项
const columns: ColumnProps[] = [
  {
    prop: 'userName',
    label: '用户',
    search: { el: 'input', props: { placeholder: '请输入用户姓名' } },
  },
  {
    prop: 'userPhone',
    label: '手机号',
    search: { el: 'input', props: { placeholder: '请输入手机号' } },
  },
  { prop: 'apartmentName', label: '公寓' },
  { prop: 'roomNumber', label: '房间号' },
  { prop: 'createTimeStr', label: '提交时间' },
  {
    prop: 'status',
    label: '报修状态',
    render: ({ row }: { row: RepairInfoInterface }) => {
      switch (row.status) {
        case RepairStatus.PENDING:
          return (
            <el-tag type="primary">
              {getLabelByValue(RepairStatusMap, row.status)}
            </el-tag>
          )
        case RepairStatus.COMPLETED:
          return (
            <el-tag type="success">
              {getLabelByValue(RepairStatusMap, row.status)}
            </el-tag>
          )
        case RepairStatus.CANCELED:
          return (
            <el-tag type="info">
              {getLabelByValue(RepairStatusMap, row.status)}
            </el-tag>
          )
        default:
          return (
            <el-tag type="info">
              {getLabelByValue(RepairStatusMap, row.status)}
            </el-tag>
          )
      }
    },
  },
  {
    prop: 'repairContent',
    label: '报修信息',
    render: ({ row }: { row: RepairInfoInterface }) => {
      return (
        <el-button type="primary" link onClick={() => showContentHandle(row)}>
          查看
        </el-button>
      )
    },
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 180 },
]

// *查询参数
const initParam = reactive({})

// 处理返回的数据格式
const dataCallback = (data: any) => {
  return {
    list: data?.records,
    total: data?.total,
  }
}

// 报修信息查看弹窗
const contentDialogVisible = ref(false)
const currentContent = ref('')
function showContentHandle(row: RepairInfoInterface) {
  currentContent.value = row.repairContent
  contentDialogVisible.value = true
}

// 撤销
const cancelRepairHandle = async (row: RepairInfoInterface) => {
  await useHandleData(
    updateRepairStatusById,
    { id: row.id, status: RepairStatus.CANCELED },
    `撤销`,
  )
  proTable.value?.getTableList()
}

// 办结
const completeRepairHandle = async (row: RepairInfoInterface) => {
  await useHandleData(
    updateRepairStatusById,
    { id: row.id, status: RepairStatus.COMPLETED },
    `办结`,
  )
  proTable.value?.getTableList()
}
</script>
