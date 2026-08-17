<template>
  <el-card>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getAppointmentInfoList"
      :initParam="initParam"
      :stripe="true"
    >
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          :type="
            scope.row.appointmentStatus === AppointmentStatus.WAITING
              ? 'primary'
              : 'info'
          "
          icon="View"
          :disabled="scope.row.appointmentStatus !== AppointmentStatus.WAITING"
          @click="takeLookHandle(scope.row)"
        >
          带看
        </el-button>
      </template>
    </ProTable>
  </el-card>
</template>

<script setup lang="tsx">
import { onMounted, reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import ProTable from '@/components/ProTable/src/ProTable.vue'
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
  AppointmentStatus,
  getLabelByValue,
  AppointmentStatusMap,
} from '@/enums/constEnums'
import { AppointmentInfoInterface } from '@/api/rentManagement/types'
import {
  getAppointmentInfoList,
  updateAppointmentStatusById,
} from '@/api/rentManagement'
import { ElMessage } from 'element-plus'

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
  { prop: 'apartmentInfo.provinceName', label: '所处省份' },
  { prop: 'apartmentInfo.cityName', label: '所处城市' },
  { prop: 'apartmentInfo.districtName', label: '所处区域' },
  { prop: 'apartmentInfo.name', label: '所处公寓' },
  { prop: 'appointmentTime', label: '预约时间' },
  { prop: 'additionalInfo', label: '备注信息' },
  {
    prop: 'appointmentStatus',
    label: '预约状态',
    render: ({ row }: { row: AppointmentInfoInterface }) => {
      switch (row.appointmentStatus) {
        case AppointmentStatus.WAITING:
          return (
            <el-tag type="success">
              {getLabelByValue(AppointmentStatusMap, row.appointmentStatus)}
            </el-tag>
          )
        case AppointmentStatus.CANCELLED:
          return (
            <el-tag type="warning">
              {getLabelByValue(AppointmentStatusMap, row.appointmentStatus)}
            </el-tag>
          )
        case AppointmentStatus.VIEWED:
          return (
            <el-tag type="info">
              {getLabelByValue(AppointmentStatusMap, row.appointmentStatus)}
            </el-tag>
          )
        default:
          return (
            <el-tag type="info">
              {getLabelByValue(AppointmentStatusMap, row.appointmentStatus) ||
                '—'}
            </el-tag>
          )
      }
    },
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 150 },
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
// 带看
const takeLookHandle = async (row: AppointmentInfoInterface) => {
  try {
    await updateAppointmentStatusById(row.id, AppointmentStatus.VIEWED)
    proTable.value?.getTableList()
    //   操作成功
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
onMounted(() => {
  // 获取省份
  getProvinceListHandle()
})
</script>
