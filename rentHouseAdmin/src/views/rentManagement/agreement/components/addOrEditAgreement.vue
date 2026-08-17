<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>{{ formData.id ? '修改' : '新增' }}租约</span>
      </div>
    </template>
    <el-form
      ref="apartmentFormRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      style="max-width: 1000px"
      status-icon
    >
      <el-form-item label="姓名" prop="name">
        <el-input v-model.trim="formData.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input v-model.trim="formData.phone" placeholder="请输入手机号码" />
      </el-form-item>
      <el-form-item label="身份证号码" prop="identificationNumber">
        <el-input
          v-model.trim="formData.identificationNumber"
          placeholder="请输入身份证号码"
        />
      </el-form-item>
      <el-form-item label="房间" required>
        <div class="flex-center">
          <el-form-item prop="provinceId">
            <el-select
              v-model="areaInfo.provinceId"
              placeholder="请选择省份"
              clearable
              @change="provinceChangeCallback"
              @clear="provinceClearCallback"
            >
              <el-option
                v-for="item in areaInfo.provinceList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="cityId">
            <el-select
              v-model="areaInfo.cityId"
              placeholder="请选择城市"
              clearable
              @change="cityChangeCallback"
              @clear="cityClearCallback"
            >
              <el-option
                v-for="item in areaInfo.cityList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="districtId">
            <el-select
              v-model="areaInfo.districtId"
              placeholder="请选择区域"
              clearable
              @change="districtChangeCallback"
              @clear="districtClearCallback"
            >
              <el-option
                v-for="item in areaInfo.districtList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="apartmentId">
            <el-select
              v-model="areaInfo.apartmentId"
              placeholder="请选择公寓"
              clearable
              filterable
              @change="apartmentChangeCallback"
              @clear="apartmentClearCallback"
            >
              <el-option
                v-for="item in areaInfo.apartmentList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="roomId">
            <el-select
              v-model="areaInfo.roomId"
              placeholder="请选择房间"
              clearable
              filterable
              @change="roomChangeCallback"
              @clear="roomClearCallback"
            >
              <el-option
                v-for="item in areaInfo.roomList"
                :key="item.id"
                :label="item.roomNumber"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form-item>

      <el-form-item label="开始时间" prop="leaseStartDate">
        <el-date-picker
          v-model="formData.leaseStartDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          placeholder="开始时间"
          clearable
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="leaseEndDate">
        <el-date-picker
          v-model="formData.leaseEndDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          placeholder="结束时间"
          clearable
        />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentTypeIds">
        <el-select
          style="width: 100%"
          v-model="formData.paymentTypeId"
          placeholder="请选择支付方式"
          clearable
        >
          <el-option
            v-for="item in paymentInfoList"
            :key="item.id"
            :label="`${item.name} (${item.additionalInfo})`"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="可选租期" prop="leaseTermIds">
        <el-select
          style="width: 100%"
          v-model="formData.leaseTermId"
          placeholder="请选择租期"
          clearable
        >
          <el-option
            v-for="item in leaseTermInfoList"
            :key="item.id"
            :label="`${item.monthCount}${item.unit}`"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="租金（元/月）" prop="rent">
        <el-input-number v-model="formData.rent" :step="1" step-strictly />
      </el-form-item>
      <el-form-item label="押金（元）" prop="deposit">
        <el-input-number v-model="formData.deposit" :step="1" step-strictly />
      </el-form-item>
      <el-form-item label="备注信息" prop="additionalInfo">
        <el-input
          v-model.trim="formData.additionalInfo"
          type="textarea"
          placeholder="请输入备注信息"
        />
      </el-form-item>
    </el-form>
    <div class="flex-center m-t-20">
      <el-button style="width: 150px" type="info" @click="router.back()">
        取消
      </el-button>
      <el-button style="width: 150px" type="primary" @click="submitHandle">
        {{ formData.id ? '保存' : '新增' }}
      </el-button>
    </div>
  </el-card>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import {
  ApartmentInterface,
  PaymentInfoInterface,
  RegionInterface,
  TermInfoInterface,
} from '@/api/apartmentManagement/types'
import {
  getApartmentListByDistrictId,
  getCityList,
  getDistrictList,
  getPaymentList,
  getProvinceList,
  getTermList,
} from '@/api/apartmentManagement'
import { useRoute, useRouter } from 'vue-router'
import { AgreementSource, AgreementStatus } from '@/enums/constEnums'
import {
  AgreementInterface,
  RoomInfoInterface,
} from '@/api/rentManagement/types'
import {
  getAgreementInfoById,
  getRoomListByApartmentId,
  saveOrUpdateAgreementInfo,
} from '@/api/rentManagement'
const route = useRoute()
const router = useRouter()
//#region <表单相关>
// 表单数据
const apartmentFormRef = ref<FormInstance>()
const formData = ref<AgreementInterface>({
  id: '',
  name: '',
  phone: '',
  identificationNumber: '',
  apartmentId: '',
  roomId: '',
  leaseStartDate: '',
  leaseEndDate: '',
  leaseTermId: '',
  rent: 0,
  deposit: 0,
  paymentTypeId: '',
  status: AgreementStatus.WAITING,
  sourceType: AgreementSource.NEW,
  additionalInfo: '',
})
// 表单验证规则
const rules = reactive({
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
})
//#endregion
//#region <省市区查询相关>
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
  // 房间的数据
  roomList: [] as RoomInfoInterface[],
  roomId: '',
})
// 监视地区信息变化,更新公寓信息
watch(
  () => areaInfo,
  (newVal) => {
    formData.value.apartmentId = newVal.apartmentId
    formData.value.roomId = newVal.roomId
  },
  { immediate: true, deep: true },
)
// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList()
    areaInfo.provinceList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取城市
async function getCityListHandle(
  provinceId: number | string = areaInfo.provinceId,
) {
  try {
    const { data } = await getCityList(provinceId)
    areaInfo.cityList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取区域
async function getDistrictListHandle(
  cityId: number | string = areaInfo.cityId,
) {
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取公寓
async function getApartmentListHandle(
  districtId: number | string = areaInfo.districtId,
) {
  try {
    const { data } = await getApartmentListByDistrictId(districtId)
    areaInfo.apartmentList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取房间
async function getRoomListHandle(
  apartmentId: number | string = areaInfo.apartmentId,
) {
  try {
    const { data } = await getRoomListByApartmentId(apartmentId)
    areaInfo.roomList = data
  } catch (error) {
    console.log(error)
  }
}
// 重置市数据
function resetCity() {
  areaInfo.cityId = ''
  areaInfo.cityList = []
}
// 重置区数据
function resetDistrict() {
  areaInfo.districtId = ''
  areaInfo.districtList = []
}
// 重置公寓数据
function resetApartment() {
  areaInfo.apartmentId = ''
  areaInfo.apartmentList = []
}
// 重置房间的数据
function resetRoom() {
  areaInfo.roomId = ''
  areaInfo.roomList = []
}
// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = areaInfo.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetApartment()
    resetRoom()
    await getCityListHandle(provinceId)
  }
}
// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  resetCity()
  resetDistrict()
  resetApartment()
  resetRoom()
}
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = areaInfo.cityId
  if (cityId) {
    resetDistrict()
    resetApartment()
    resetRoom()
    await getDistrictListHandle(cityId)
  }
}
// 城市清除回调
const cityClearCallback = () => {
  console.log('清空城市')
  areaInfo.cityId = ''
  resetDistrict()
  resetApartment()
  resetRoom()
}
// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = areaInfo.districtId
  if (districtId) {
    resetApartment()
    resetRoom()
    await getApartmentListHandle(districtId)
  }
}
// 区域清除回调
const districtClearCallback = () => {
  console.log('清空区域')
  areaInfo.districtId = ''
  resetApartment()
  resetRoom()
}
// 公寓改变回调
const apartmentChangeCallback = async () => {
  console.log('公寓改变')
  let apartmentId = areaInfo.apartmentId
  if (apartmentId) {
    resetRoom()
    await getRoomListHandle(apartmentId)
  }
}
// 公寓清除回调
const apartmentClearCallback = () => {
  console.log('清空公寓')
  areaInfo.apartmentId = ''
  resetRoom()
}
// 房间改变回调
const roomChangeCallback = async () => {
  console.log('房间改变')
}
// 房间清除回调
const roomClearCallback = () => {
  console.log('清空房间')
  areaInfo.roomId = ''
}
//#endregion
//#region <支付方式、可选租期>
// 支付方式信息
const paymentInfoList = ref<PaymentInfoInterface[]>([])
// 可选租期信息
const leaseTermInfoList = ref<TermInfoInterface[]>([])
// 获取支付方式信息列表
async function getPaymentInfoListHandle() {
  try {
    const { data } = await getPaymentList()
    paymentInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 获取可选租期信息列表
async function getLeaseTermInfoListHandle() {
  try {
    const { data } = await getTermList()
    leaseTermInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

//#endregion

// 根据id获取房间信息
async function getAgreementInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getAgreementInfoById(id)
    formData.value = data
    // 重置省市区
    // 获取城市
    areaInfo.provinceId = data.apartmentInfo.provinceId as string
    areaInfo.cityId = data.apartmentInfo.cityId as string
    areaInfo.districtId = data.apartmentInfo.districtId as string
    areaInfo.apartmentId = data.apartmentId as string
    areaInfo.roomId = data.roomId as string
    data.apartmentInfo.provinceId &&
      (await getCityListHandle(data.apartmentInfo.provinceId))
    // 获取区域
    data.apartmentInfo.cityId &&
      (await getDistrictListHandle(data.apartmentInfo.cityId))
    // 获取公寓
    data.apartmentInfo.districtId &&
      (await getApartmentListHandle(data.apartmentInfo.districtId))
    // 获取房间
    data.apartmentId && (await getRoomListHandle(data.apartmentId))
  } catch (error) {
    console.log(error)
  }
}
// 新增或更新公寓信息
async function addOrUpdateAgreementInfoHandle() {
  try {
    await saveOrUpdateAgreementInfo(formData.value)
    ElMessage.success('操作成功')
    router.back()
  } catch (error) {
    console.log(error)
  }
}
// 提交
function submitHandle() {
  apartmentFormRef.value?.validate(async (valid) => {
    if (valid) {
      await addOrUpdateAgreementInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}
onMounted(() => {
  // 获取省份信息列表
  getProvinceListHandle()
  // 获取支付方式信息列表
  getPaymentInfoListHandle()
  // 获取可选租期信息列表
  getLeaseTermInfoListHandle()
  if (route.query?.id) {
    getAgreementInfoByIdHandle(route.query.id as string)
  }
})
</script>

<style scoped lang="scss">
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
