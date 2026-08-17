<template>
  <div>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getSysPostListPaginate"
      :initParam="initParam"
    >
      <template #tableHeader>
        <el-button type="primary" icon="Plus" @click="openDialog(0)">
          添加
        </el-button>
      </template>
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          type="primary"
          link
          icon="Edit"
          @click="openDialog(1, scope.row)"
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
    <PostDialog ref="DialogRef" />
  </div>
</template>

<script setup lang="tsx">
import { reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import PostDialog from './components/PostDialog.vue'
import { useHandleData } from '@/hooks/useHandleData'
import {
  addSysPost,
  deleteSysPostById,
  getSysPostListPaginate,
  updateSysPost,
  updateSysPostStatus,
} from '@/api/system'
import { PostInterfacesRes } from '@/api/system/types'

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

// 打开Dialog
const DialogRef = ref()
const openDialog = (
  type: number, // 0 新增 1 编辑
  rowData: PostInterfacesRes = {} as PostInterfacesRes,
) => {
  let initRowData = {
    postCode: '',
    name: '',
    description: '',
    status: 1,
    id: '' as unknown as number,
  } as PostInterfacesRes
  const params = {
    rowData: type == 0 ? { ...rowData, ...initRowData } : { ...rowData },
    api: type == 0 ? addSysPost : updateSysPost,
    getTableList: proTable.value?.getTableList,
  }
  console.log('params', params)
  DialogRef.value.acceptParams(params)
}

// *根据id删除用户
const handleDelete = async (row: PostInterfacesRes) => {
  await useHandleData(deleteSysPostById, row.id, `删除${row.name}`)
  proTable.value?.getTableList()
}

// *表格配置项
const columns: ColumnProps[] = [
  { type: 'index', label: '序号', width: 80 },
  {
    prop: 'name',
    label: '岗位名称',
  },
  {
    prop: 'postCode',
    label: '岗位编码',
  },
  {
    prop: 'description',
    label: '岗位描述',
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    enum: [
      { label: '正常', value: 1 },
      { label: '停用', value: 0 },
    ],
    render: ({ row }) => {
      return (
        <el-switch
          active-value={1}
          inactive-value={0}
          v-model={row.status}
          onChange={() => updateSysPostStatus(row.id, row.status)}
        ></el-switch>
      )
    },
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 280 },
]
</script>
