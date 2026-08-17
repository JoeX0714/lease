<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>{{ formData.id ? '修改' : '新增' }}公寓</span>
      </div>
    </template>
    <el-form
      ref="apartmentFormRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      style="max-width: 660px"
      status-icon
    >
      <el-form-item label="公寓名称" prop="name">
        <el-input v-model="formData.name" />
      </el-form-item>
      <el-form-item label="公寓介绍" prop="introduction">
        <el-input type="textarea" v-model="formData.introduction" />
      </el-form-item>
      <el-form-item label="所处区域" required>
        <div class="flex-center">
          <el-form-item prop="provinceId">
            <el-select
              v-model="formData.provinceId"
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
              v-model="formData.cityId"
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
              v-model="formData.districtId"
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
        </div>
      </el-form-item>
      <el-form-item label="详细地址" prop="addressDetail">
        <el-select
          v-model="formData.addressDetail"
          filterable
          remote
          clearable
          placeholder="请输入详细地址查询"
          :remote-method="remoteMethod"
          style="width: 100%"
          @change="addressDetailChangeCallback"
        >
          <el-option
            v-for="item in addressDetailOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="公寓前台电话" prop="phone">
        <el-input v-model="formData.phone" />
      </el-form-item>
      <el-form-item label="是否发布" prop="isRelease">
        <el-radio-group v-model="formData.isRelease" class="ml-4">
          <el-radio :label="ApartmentReleaseStatus.NOT_RELEASED">
            {{
              getLabelByValue(
                ApartmentReleaseStatusMap,
                ApartmentReleaseStatus.NOT_RELEASED,
              )
            }}
          </el-radio>
          <el-radio :label="ApartmentReleaseStatus.RELEASED">
            {{
              getLabelByValue(
                ApartmentReleaseStatusMap,
                ApartmentReleaseStatus.RELEASED,
              )
            }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="公寓配套" prop="facilityInfoIds">
        <el-select
          style="width: 100%"
          v-model="formData.facilityInfoIds"
          placeholder="请选择公寓配套"
          multiple
          clearable
        >
          <el-option
            v-for="item in facilityInfoList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="公寓标签" prop="labelIds">
        <el-select
          style="width: 100%"
          v-model="formData.labelIds"
          placeholder="请选择公寓标签"
          multiple
          clearable
        >
          <el-option
            v-for="item in labelInfoList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="公寓杂费" prop="feeValueIds">
        <el-tree-select
          style="width: 100%"
          v-model="formData.feeValueIds"
          placeholder="请选择公寓杂费"
          :data="feeInfoList"
          multiple
          clearable
          node-key="value"
          :render-after-expand="false"
          @node-click="feeNodeClickHandle"
        ></el-tree-select>
      </el-form-item>
      <el-form-item label="图片" prop="graphVoList">
        <upload-img
          v-model:file-list="formData.graphVoList"
          :on-success="uploadSuccessHandle"
          list-type="picture-card"
          :limit="5"
        ></upload-img>
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
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, FormInstance, UploadFiles } from 'element-plus'
import {
  AddressOptionsInterface,
  ApartmentInterface,
  FacilityInfoInterface,
  LabelInfoInterface,
  RegionInterface,
} from '@/api/apartmentManagement/types'
import {
  ApartmentReleaseStatus,
  getLabelByValue,
  ApartmentReleaseStatusMap,
  BuildingType,
} from '@/enums/constEnums'
import {
  getApartmentById,
  getCityList,
  getDistrictList,
  getFacilityInfoList,
  getFeeInfoList,
  getLabelInfoList,
  getProvinceList,
  saveOrUpdateApartment,
} from '@/api/apartmentManagement'
import { useMap } from '@/hooks/useMap'
import { TreeData } from '@/api/apartmentManagement/types'
// import { ElTree } from 'element-plus/es/components/tree'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'
import UploadImg from '@/components/uploadImg/uploadImg.vue'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
//#region <表单相关>
// 表单数据
type FormDataInstance = Required<
  Omit<
    ApartmentInterface,
    | 'totalRoomCount'
    | 'freeRoomCount'
    | 'facilityInfoList'
    | 'labelInfoList'
    | 'feeValueVoList'
  >
>
const apartmentFormRef = ref<FormInstance>()
const formData = ref<FormDataInstance>({
  id: '',
  name: '',
  introduction: '',
  districtId: '',
  districtName: '',
  cityId: '',
  cityName: '',
  provinceId: '',
  provinceName: '',
  addressDetail: '',
  latitude: '',
  longitude: '',
  phone: '',
  isRelease: ApartmentReleaseStatus.NOT_RELEASED,
  facilityInfoIds: [],
  labelIds: [],
  feeValueIds: [],
  graphVoList: [
    // {
    //   name: 'food.jpeg',
    //   url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
    // },
  ] as UploadFile[],
})
// 表单验证规则
const rules = reactive({
  name: [{ required: true, message: '请输入公寓名称', trigger: 'blur' }],
  introduction: [
    { required: true, message: '请输入公寓介绍', trigger: 'blur' },
  ],
  provinceId: [{ required: true, message: '请选择省份', trigger: 'change' }],
  cityId: [{ required: true, message: '请选择城市', trigger: 'change' }],
  districtId: [{ required: true, message: '请选择区域', trigger: 'change' }],
  addressDetail: [
    { required: true, message: '请选择详细地址', trigger: 'change' },
  ],
  phone: [{ required: true, message: '请输入公寓前台电话', trigger: 'blur' }],
  graphVoList: [{ required: true, message: '请上传图片', trigger: 'change' }],
})
//#region <地区数据相关>
// 地区数据
const areaInfo = reactive({
  // 省份数据
  provinceList: [] as RegionInterface[],
  // 市区数据
  cityList: [] as RegionInterface[],
  // 区县数据
  districtList: [] as RegionInterface[],
})
//#region <省市区查询相关>
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
  provinceId: number | string = formData.value.provinceId,
) {
  if (!provinceId) return
  try {
    const { data } = await getCityList(provinceId)
    areaInfo.cityList = data
  } catch (error) {
    console.log(error)
  }
}

// 获取区域
async function getDistrictListHandle(
  cityId: number | string = formData.value.cityId,
) {
  if (!cityId) return
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
  } catch (error) {
    console.log(error)
  }
}

// 重置市数据
function resetCity() {
  formData.value.cityId = ''
  areaInfo.cityList = []
}
// 重置区数据
function resetDistrict() {
  formData.value.districtId = ''
  areaInfo.districtList = []
}
// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = formData.value.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    await getCityListHandle(provinceId)
  }
}
// 省份清除回调
const provinceClearCallback = () => {
  formData.value.provinceId = ''
  resetCity()
  resetDistrict()
}
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = formData.value.cityId
  if (cityId) {
    resetDistrict()
    await getDistrictListHandle(cityId)
  }
}
// 城市清除回调
const cityClearCallback = () => {
  console.log('清空城市')
  formData.value.cityId = ''
  resetDistrict()
}
// 区域改变回调
const districtChangeCallback = async () => {
  console.log('区域改变')
}
// 区域清除回调
const districtClearCallback = () => {
  console.log('清空区域')
  formData.value.districtId = ''
}
//#endregion
//#endregion

//#endregion
//#region <高德地图相关>
// 详细地址建议列表
const addressDetailOptions = ref<AddressOptionsInterface[]>([])

// 地图实例
const { AMap } = useMap()
// 动态地址改查询
function remoteMethod(keywords: string) {
  if (keywords.trim()) {
    const provinceName =
      areaInfo.provinceList.find(
        (item) => item.id === formData.value.provinceId,
      )?.name || ''

    const districtName =
      areaInfo.districtList.find(
        (item) => item.id === formData.value.districtId,
      )?.name || ''
    keywords = provinceName + districtName + keywords
    AMap.value.plugin('AMap.AutoComplete', function () {
      // 实例化Autocomplete
      let autoOptions = {
        city: '全国',
      }
      console.log('AMap', AMap.value)
      console.log('keywords', keywords)
      let autoComplete = new AMap.value.AutoComplete(autoOptions)
      autoComplete.search(keywords, function (status: any, result: any) {
        // 搜索成功时，result即是对应的匹配数据
        console.log('result', result)
        addressDetailOptions.value =
          result?.tips?.map((item: any) => {
            return {
              label: item.district + item.name + item.address,
              value: item.district + item.name + item.address,
              location: item.location,
            }
          }) || []
      })
    })
  } else {
    addressDetailOptions.value = []
  }
}
// 详细地址改变回调
function addressDetailChangeCallback(value: string | number) {
  const targetObj = addressDetailOptions.value.find(
    (item) => item.value === value,
  )
  if (targetObj) {
    // lng  经度
    formData.value.longitude = targetObj.location?.lng || ''
    // lat  纬度
    formData.value.latitude = targetObj.location?.lat || ''
  }
}
//#endregion
//#region <配套信息、标签信息、杂费信息>
// 配套信息
const facilityInfoList = ref<FacilityInfoInterface[]>([])
// 标签信息
const labelInfoList = ref<LabelInfoInterface[]>([])
// 杂费信息
const feeInfoList = ref<TreeData[]>([])
// const feeTreeSelectRef = ref<InstanceType<typeof ElTree>>()
// 获取配套信息
async function getFacilityInfoListHandle() {
  try {
    const { data } = await getFacilityInfoList(BuildingType.APARTMENT)
    facilityInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 获取标签信息
async function getLabelInfoListHandle() {
  try {
    const { data } = await getLabelInfoList(BuildingType.APARTMENT)
    labelInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 获取杂费信息
async function getFeeInfoListHandle() {
  try {
    const { data } = await getFeeInfoList()
    feeInfoList.value = data.map((item) => {
      return {
        value: item.id + item.name,
        label: item.name,
        children: item.feeValueList.map((child) => {
          return {
            value: child.id,
            label: `${child.name} ${child.unit}(${item.name})`,
            parentId: item.id + item.name,
          }
        }),
      }
    })
  } catch (error) {
    console.log(error)
  }
}
// 配套信息节点点击回调
function feeNodeClickHandle(data: TreeData) {
  //   保证每个子节点只有一个被选中
  if (data.parentId) {
    //   收集当前父节点下所有的子节点列表
    const childrenList = feeInfoList.value
      .find((item) => item.value === data.parentId)
      ?.children?.map((item) => item.value)
    nextTick(() => {
      //   遍历feeValueIds节点，删除所有childrenList中的值
      formData.value.feeValueIds = formData.value.feeValueIds?.filter(
        (item) => !childrenList?.includes(item),
      )
      //   将当前节点的值添加到feeValueIds中
      formData.value.feeValueIds?.push(data.value as number)
    })
  }
}
//#endregion
// 图片上传成功
function uploadSuccessHandle(
  response: any,
  uploadFile: UploadFile,
  uploadFiles: UploadFiles,
) {
  // 仅后端业务成功且返回真实图片地址时写入；否则移除失败文件并提示，禁止把 blob 预览地址写入提交数据
  if (response?.code !== 200 || !response?.data) {
    formData.value.graphVoList = formData.value.graphVoList?.filter(
      (item) => item.uid !== uploadFile.uid,
    )
    ElMessage.error('图片上传失败')
    return
  }
  formData.value.graphVoList = uploadFiles?.map((item) => {
    return {
      ...item,
      url: (item?.response as any)?.data || item.url,
    }
  })
}
// 根据id获取公寓信息
async function getApartmentInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getApartmentById(id)
    // 构造表单数据
    data.facilityInfoIds = data.facilityInfoList?.map(
      (item) => item.id,
    ) as number[]
    delete data.facilityInfoList
    data.labelIds = data.labelInfoList?.map((item) => item.id) as number[]
    delete data.labelInfoList
    data.feeValueIds = data.feeValueVoList?.map((item) => item.id) as number[]
    delete data.feeValueVoList
    addressDetailOptions.value = [
      {
        label: data.addressDetail,
        value: data.addressDetail,
        location: {
          lng: data.longitude,
          lat: data.latitude,
        },
      },
    ]
    formData.value = data as FormDataInstance
    // 重置省市区
    // 获取城市
    console.log(formData.value.provinceId, formData.value.cityId)
    formData.value.provinceId && getCityListHandle(formData.value.provinceId)
    // 获取区域
    formData.value.cityId && getDistrictListHandle(formData.value.cityId)
  } catch (error) {
    console.log(error)
  }
}
// 新增或更新公寓信息
async function addOrUpdateApartmentInfoHandle() {
  try {
    await saveOrUpdateApartment(formData.value)
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
      await addOrUpdateApartmentInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}
onMounted(() => {
  // 获取省份
  getProvinceListHandle()
  // 获取配套信息
  getFacilityInfoListHandle()
  // 获取标签信息
  getLabelInfoListHandle()
  // 获取杂费信息
  getFeeInfoListHandle()
  if (route.query?.id) {
    getApartmentInfoByIdHandle(route.query.id as string)
  }
})
</script>

<style scoped lang="scss">
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
