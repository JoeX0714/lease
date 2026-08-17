<template>
  <el-card>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getUserManagementInfoList"
      :initParam="initParam"
      :stripe="true"
    ></ProTable>
  </el-card>
</template>

<script setup lang="tsx">
import { reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import ProTable from '@/components/ProTable/src/ProTable.vue'
import { UserStatus, UserStatusMap } from '@/enums/constEnums'
import {
  getUserManagementInfoList,
  updateUserManagementStatus,
} from '@/api/userManagement'
import { UserManagementInfoInterface } from '@/api/userManagement/types'

// *获取 ProTable 元素，调用其获取刷新数据方法
const proTable = ref<InstanceType<typeof ProTable>>()

// *表格配置项
const columns: ColumnProps[] = [
  {
    prop: 'nickname',
    label: '昵称',
  },
  {
    prop: 'avatarUrl',
    label: '头像',
    render: ({ row }: { row: UserManagementInfoInterface }) => {
      return (
        <el-image
          style={{ width: '60px', height: '60px' }}
          src={row.avatarUrl}
          fit="cover"
        ></el-image>
      )
    },
  },
  {
    prop: 'phone',
    label: '手机',
    search: { el: 'input', props: { placeholder: '请输入手机号' } },
  },
  {
    prop: 'status',
    label: '帐号状态',
    enum: UserStatusMap,
    search: {
      el: 'select',
      props: { placeholder: '请选择帐号状态' },
    },
    render: ({ row }: { row: UserManagementInfoInterface }) => {
      return (
        <el-switch
          active-value={UserStatus.NORMAL}
          inactive-value={UserStatus.DISABLED}
          v-model={row.status}
          onChange={() =>
            updateUserManagementStatus(row.id, row.status as UserStatus)
          }
        ></el-switch>
      )
    },
  },
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
</script>
