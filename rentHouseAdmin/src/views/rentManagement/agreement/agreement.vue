<template>
  <el-card>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getAgreementInfoList"
      :initParam="initParam"
      :stripe="true"
    >
      <template #tableHeader>
        <el-button type="primary" icon="Plus" @click="addHandle">
          新增租约
        </el-button>
      </template>
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button type="success" icon="Edit" @click="editHandle(scope.row)">
          修改
        </el-button>
        <el-button type="danger" icon="Delete" @click="deleteHandle(scope.row)">
          删除
        </el-button>
        <el-button
          type="warning"
          icon="Warning"
          v-if="scope.row.status === AgreementStatus.RENEW_TO_BE_CONFIRMED"
          @click="confirmLeaseHandle(scope.row)"
        >
          确认续约
        </el-button>
        <el-button
          type="warning"
          icon="Warning"
          v-if="scope.row.status === AgreementStatus.WAITING"
          @click="cancelLeaseHandle(scope.row)"
        >
          取消租约
        </el-button>
        <el-button
          type="danger"
          icon="Warning"
          v-if="scope.row.status === AgreementStatus.TO_BE_CONFIRMED"
          @click="confirmThrowLeaseHandle(scope.row)"
        >
          确认退租
        </el-button>
      </template>
    </ProTable>
  </el-card>
</template>

<script setup lang="tsx">
import { onMounted, reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import ProTable from '@/components/ProTable/src/ProTable.vue'
import { useRouter } from 'vue-router'
import { useHandleData } from '@/hooks/useHandleData'
import {
  getApartmentListByDistrictId,
  getCityList,
  getDistrictList,
  getProvinceList,
} from '@/api/apartmentManagement'
import {
  ApartmentInterface,
  RegionInterface,
} from '@/api/apartmentManagement/types'
import {
  AgreementSourceMap,
  AgreementStatus,
  AgreementStatusMap,
  getLabelByValue,
} from '@/enums/constEnums'
import {
  deleteAgreementInfoById,
  getAgreementInfoList,
  updateAgreementStatusById,
} from '@/api/rentManagement'
import { AgreementInfoInterface } from '@/api/rentManagement/types'
const router = useRouter()

// *获取 ProTable 元素，调用其获取刷新数据方法
const proTable = ref<InstanceType<typeof ProTable>>()
// 地区数据
const areaInfo = reactive({
  // 省份数据
  provinceList: [] as RegionInterface[],
  provinceId: '',
  // 市区数据
  cityList: [] as RegionInterface[],
  cityId: '',
  // 区县数据
  districtList: [] as RegionInterface[],
  districtId: '',
  // 公寓数据
  apartmentList: [] as ApartmentInterface[],
  apartmentId: '',
})
// *表格配置项
const columns: ColumnProps[] = [
  {
    prop: 'name',
    label: '姓名',
    search: { el: 'input', props: { placeholder: '请输入姓名' } },
  },
  {
    prop: 'phone',
    label: '手机',
    search: { el: 'input', props: { placeholder: '请输入手机号' } },
  },
  {
    prop: 'roomNumber',
    label: '房间',
    search: { el: 'input', props: { placeholder: '请输入房间号' } },
    render: ({ row }: { row: AgreementInfoInterface }) => {
      return row.roomInfo.roomNumber
    },
  },
  {
    prop: 'provinceId',
    label: '省份',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.provinceId}
            placeholder="请选择省份"
            clearable
            key="provinceId"
            onChange={provinceChangeCallback}
            onClear={provinceClearCallback}
          >
            {areaInfo.provinceList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  {
    prop: 'cityId',
    label: '城市',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      key: 'cityId',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.cityId}
            placeholder="请选择城市"
            clearable
            key="cityId"
            onChange={cityChangeCallback}
            onClear={cityClearCallback}
          >
            {areaInfo.cityList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  {
    prop: 'districtId',
    label: '区域',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      key: 'districtId',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.districtId}
            placeholder="请选择区域"
            clearable
            onChange={districtChangeCallback}
            onClear={districtClearCallback}
          >
            {areaInfo.districtList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  {
    prop: 'apartmentId',
    label: '公寓',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      key: 'apartmentId',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.apartmentId}
            placeholder="请选择公寓"
            clearable
            onChange={apartmentChangeCallback}
            onClear={apartmentClearCallback}
          >
            {areaInfo.apartmentList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  { prop: 'apartmentInfo.name', label: '签约公寓' },
  { prop: 'apartmentInfo.provinceName', label: '所处省份' },
  { prop: 'apartmentInfo.cityName', label: '所处城市' },
  { prop: 'apartmentInfo.districtName', label: '所处区域' },
  {
    prop: 'leaseStartDate',
    label: '租约周期',
    width: 200,
    render: ({ row }: { row: AgreementInfoInterface }) => {
      return row.leaseStartDate + ' ～ ' + row.leaseEndDate
    },
  },
  { prop: 'rent', label: '租金（元/月）' },
  { prop: 'deposit', label: '押金（元）' },
  {
    prop: 'paymentType.name',
    label: '支付方式',
  },
  { prop: 'leaseTerm.monthCount', label: '租期（月）' },
  {
    prop: 'sourceType',
    label: '租约来源',
    render: ({ row }: { row: AgreementInfoInterface }) => {
      return getLabelByValue(AgreementSourceMap, row.sourceType)
    },
  },
  {
    prop: 'status',
    label: '租约状态',
    render: ({ row }: { row: AgreementInfoInterface }) => {
      switch (row.status) {
        case AgreementStatus.WAITING:
          return (
            <el-tag type="warning">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        case AgreementStatus.RENEW_TO_BE_CONFIRMED:
          return (
            <el-tag type="warning">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        case AgreementStatus.SIGNED:
          return (
            <el-tag type="success">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        case AgreementStatus.TO_BE_CONFIRMED:
          return (
            <el-tag type="danger">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        default:
          return (
            <el-tag type="info">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
      }
    },
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 320 },
]

// *查询参数
const initParam = reactive({})

//#region <省市区查询相关>
// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList()
    areaInfo.provinceList = data
    proTable.value?.enumMap.set('provinceId', areaInfo.provinceList)
  } catch (error) {
    console.log(error)
  }
}
// 获取城市
async function getCityListHandle(provinceId: number) {
  try {
    const { data } = await getCityList(provinceId)
    areaInfo.cityList = data
    proTable.value?.enumMap.set('cityId', areaInfo.cityList)
  } catch (error) {
    console.log(error)
  }
}
// 获取区域
async function getDistrictListHandle(cityId: number) {
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
    proTable.value?.enumMap.set('districtId', areaInfo.districtList)
  } catch (error) {
    console.log(error)
  }
}
// 获取公寓
async function getApartmentListHandle(districtId: number) {
  try {
    const { data } = await getApartmentListByDistrictId(districtId)
    areaInfo.apartmentList = data
    proTable.value?.enumMap.set('apartmentId', areaInfo.apartmentList)
  } catch (error) {
    console.log(error)
  }
}
// 重置市数据
function resetCity() {
  areaInfo.cityId = ''
  areaInfo.cityList = []
  proTable.value?.enumMap.set('cityId', [])
  proTable.value!.searchParam.cityId = ''
}
// 重置区数据
function resetDistrict() {
  areaInfo.districtId = ''
  areaInfo.districtList = []
  proTable.value?.enumMap.set('districtId', [])
  proTable.value!.searchParam.districtId = ''
}
// 重置公寓数据
function resetApartment() {
  areaInfo.apartmentId = ''
  areaInfo.apartmentList = []
  proTable.value?.enumMap.set('apartmentId', [])
  proTable.value!.searchParam.apartmentId = ''
}
// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = proTable.value!.searchParam.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetApartment()
    await getCityListHandle(provinceId)
  }
}
// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  proTable.value!.searchParam.provinceId = ''
  resetCity()
  resetDistrict()
  resetApartment()
}
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = proTable.value!.searchParam.cityId
  if (cityId) {
    resetDistrict()
    resetApartment()
    await getDistrictListHandle(cityId)
  }
}
// 城市清除回调
const cityClearCallback = () => {
  console.log('清空城市')
  areaInfo.cityId = ''
  proTable.value!.searchParam.cityId = ''
  resetDistrict()
  resetApartment()
}
// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = proTable.value!.searchParam.districtId
  if (districtId) {
    resetApartment()
    await getApartmentListHandle(districtId)
  }
}
// 区域清除回调
const districtClearCallback = () => {
  console.log('清空区域')
  areaInfo.districtId = ''
  proTable.value!.searchParam.districtId = ''
  resetApartment()
}
// 公寓改变回调
const apartmentChangeCallback = async () => {
  console.log('公寓改变')
}
// 公寓清除回调
const apartmentClearCallback = () => {
  console.log('清空公寓')
  areaInfo.apartmentId = ''
  proTable.value!.searchParam.apartmentId = ''
}
//#endregion

// 处理返回的数据格式
const dataCallback = (data: any) => {
  return {
    list: data?.records,
    total: data?.total,
  }
}
// 查看
const editHandle = (row: AgreementInfoInterface) => {
  router.push({
    path: '/rentManagement/agreement/addOrEditAgreement',
    query: {
      id: row.id,
    },
  })
}
// 新增
const addHandle = () => {
  router.push({
    path: '/rentManagement/agreement/addOrEditAgreement',
  })
}
// 删除
const deleteHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    deleteAgreementInfoById,
    row.id,
    `删除${row.name + row.apartmentInfo.name + row.roomInfo.roomNumber}房间`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 取消租约
const cancelLeaseHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.CANCELLED },
    `取消租约`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 确认续约
const confirmLeaseHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.SIGNED },
    `确认续约`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 确认退租
const confirmThrowLeaseHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.CONFIRMED },
    `确认退租`,
  )
  // 修改row
  proTable.value?.getTableList()
}

onMounted(() => {
  // 获取省份
  getProvinceListHandle()
})
</script>
