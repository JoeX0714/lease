<template>
  <div>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getSysUserList"
      :initParam="initParam"
    >
      <template #tableHeader="scope">
        <el-button type="primary" icon="Plus" @click="openDrawer('新增')">
          添加
        </el-button>
        <el-button
          v-if="false"
          type="danger"
          icon="Delete"
          plain
          @click="batchDelete(scope.selectedListIds)"
          :disabled="!scope.isSelected"
        >
          批量删除
        </el-button>
      </template>
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          type="primary"
          link
          icon="Edit"
          @click="openDrawer('编辑', scope.row)"
        >
          编辑
        </el-button>
        <el-button
          type="primary"
          link
          icon="Delete"
          @click="handleDelete(scope.row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <UserDrawer ref="drawerRef" />
  </div>
</template>

<script setup lang="tsx">
import { onMounted, reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import UserDrawer from './components/UserDrawer.vue'
import { useHandleData } from '@/hooks/useHandleData'
import { ElMessage } from 'element-plus'
import {
  addSysUser,
  assignSysUserRoles,
  batchSysUser,
  deleteSysUserById,
  getSysPostList,
  getSysUserList,
  getUserRolesListByUserId,
  updateSysUser,
  updateSysUserStatus,
} from '@/api/system'
import { PostInterfacesRes, SysUserInterfaceRes } from '@/api/system/types'
import {
  getLabelByValue,
  SystemUserStatus,
  SystemUserTypeMap,
} from '@/enums/constEnums'
// *获取 ProTable 元素，调用其获取刷新数据方法
const proTable = ref()

// *查询参数
const initParam = reactive({})

// 处理返回的数据格式
const dataCallback = (data: any) => {
  return {
    list: data?.records,
    total: data?.total,
  }
}

// *新增、编辑、分配角色
const drawerRef = ref()
const openDrawer = async (
  title: string,
  rowData: Partial<SysUserInterfaceRes> = {} as SysUserInterfaceRes,
) => {
  let params = {
    title,
    rowData: { ...rowData },
    api:
      title === '新增'
        ? addSysUser
        : title === '编辑'
        ? updateSysUser
        : assignSysUserRoles,
    getTableList: proTable.value?.getTableList,
    postList: postList.value,
  }
  console.log(params)
  drawerRef.value.acceptParams(params)
}
// *根据id删除用户
const handleDelete = async (row: SysUserInterfaceRes) => {
  if (row?.username === 'admin') {
    ElMessage({
      type: 'warning',
      message: `系统用户不允许删除`,
    })
    return
  }
  await useHandleData(
    deleteSysUserById,
    row.id as number,
    `删除${row.username}用户`,
  )
  proTable.value?.getTableList()
}

// *批量删除用户
const batchDelete = async (ids: string[]) => {
  await useHandleData(batchSysUser, ids, '删除所选用户信息')
  proTable.value?.clearSelection()
  proTable.value?.getTableList()
}
// 岗位列表
const postList = ref([] as PostInterfacesRes[])
// 获取全部岗位列表
const getPostList = async () => {
  try {
    const res = await getSysPostList()
    postList.value = res.data
  } catch (error) {
    console.log(error)
    ElMessage.error((error as any)?.message || 'Has Error')
  }
}
// *表格配置项
const columns: ColumnProps[] = [
  { type: 'index', label: '序号' },
  {
    prop: 'name',
    label: '员工姓名',
    search: { el: 'input', props: { placeholder: '输入员工姓名' } },
  },
  {
    prop: 'phone',
    label: '手机号码',
    search: { el: 'input', props: { placeholder: '输入手机号码' } },
  },
  { prop: 'postName', label: '岗位' },
  {
    prop: 'type',
    label: '用户类型',
    render: ({ row }) => {
      return (
        <el-tag className="flex-wrap m-r-10 m-t-5">
          {getLabelByValue(SystemUserTypeMap, row.type)}
        </el-tag>
      )
    },
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    render: ({ row }) => {
      return (
        <el-switch
          active-value={SystemUserStatus.NORMAL}
          inactive-value={SystemUserStatus.DISABLED}
          v-model={row.status}
          onChange={() => updateSysUserStatus(row.id, row.status)}
        ></el-switch>
      )
    },
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 280 },
]
onMounted(() => {
  getPostList()
})
</script>
