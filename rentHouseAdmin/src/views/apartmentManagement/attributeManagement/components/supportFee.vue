<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>杂费信息管理</span>
      </div>
    </template>
    <el-row class="container" v-for="item in attrTypeList" :key="item">
      <el-col :span="2" class="text-center">
        <el-popconfirm
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          @confirm="deleteFeeKeyHandle(item)"
          @cancel="editFeeKeyHandle(item)"
          :title="`修改或删除${item.name}`"
        >
          <template #reference>
            <div class="item m-r-10 pointer">
              <span>{{ item.name }}</span>
              <el-tooltip
                effect="dark"
                content="点击属性名可进行修改/删除操作"
                placement="top-start"
              >
                <el-icon class="m-l-5"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-popconfirm>
      </el-col>
      <el-col :span="22" class="item-container">
        <el-popconfirm
          v-for="item2 in item.feeValueList || []"
          :key="item2.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          @confirm="deleteFeeValueHandle(item2)"
          @cancel="editFeeValueHandle(item2)"
          :title="`修改或删除${item2.name}`"
        >
          <template #reference>
            <div class="item m-r-10 pointer">
              <el-tag style="padding: 13px">
                {{ item2.name + item2.unit }}
              </el-tag>
            </div>
          </template>
        </el-popconfirm>
        <el-icon
          class="item m-r-10 m-t-5 pointer"
          :size="35"
          color="#567722"
          @click="addFeeValueHandle(item)"
        >
          <CirclePlus />
        </el-icon>
      </el-col>
    </el-row>
    <el-button type="primary" class="m-t-20" @click="addFeeKeyHandle">
      添加新属性
    </el-button>
    <!--    信息修改弹窗管理-->
    <SupportFeeDialog
      ref="supportFeeDialog"
      :updateFee="getFeeInfoListHandle"
    ></SupportFeeDialog>
    <!--    属性信息弹窗管理-->
    <supportFeeAddOrEditAttrDialog
      ref="supportFeeAddOrEditFeeDialog"
      :updateFeeAddOrEditFee="getFeeInfoListHandle"
    ></supportFeeAddOrEditAttrDialog>
  </el-card>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  FeeValueInfoInterface,
  FeeInfoInterface,
} from '@/api/apartmentManagement/types'
import {
  deleteFeeKeyById,
  deleteFeeValueById,
  getFeeInfoList,
} from '@/api/apartmentManagement'
import SupportFeeDialog from '@/views/apartmentManagement/attributeManagement/components/supportFeeDialog.vue'
import { ElMessage } from 'element-plus'
import SupportFeeAddOrEditAttrDialog from '@/views/apartmentManagement/attributeManagement/components/supportFeeAddOrEditAttrDialog.vue'
const supportFeeDialog = ref<InstanceType<typeof SupportFeeDialog>>()
const supportFeeAddOrEditFeeDialog =
  ref<InstanceType<typeof SupportFeeAddOrEditAttrDialog>>()

// 属性类型
const attrTypeList = ref<FeeInfoInterface[]>([])
// 获取属性信息
async function getFeeInfoListHandle() {
  try {
    const { data } = await getFeeInfoList()
    data.forEach((item) => {
      item.feeValueList?.forEach((item2) => {
        item2.feeKeyName = item.name
      })
    })
    attrTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 删除属性值
const deleteFeeValueHandle = async (item: FeeValueInfoInterface) => {
  console.log('删除', item)
  try {
    await deleteFeeValueById(item.id)
    await getFeeInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
// 编辑属性值
const editFeeValueHandle = (item: FeeValueInfoInterface) => {
  console.log('编辑', item)
  supportFeeDialog.value?.show(item)
}
// 添加属性值
const addFeeValueHandle = (item: FeeInfoInterface) => {
  console.log('添加属性', item)
  supportFeeDialog.value?.show({
    feeKeyId: item.id,
    feeKeyName: item.name,
  })
}
// 添加新属性
const addFeeKeyHandle = () => {
  supportFeeAddOrEditFeeDialog.value?.show()
}
// 删除属性
const deleteFeeKeyHandle = async (item: FeeInfoInterface) => {
  console.log('删除', item)
  try {
    await deleteFeeKeyById(item.id)
    await getFeeInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
const editFeeKeyHandle = (item: FeeInfoInterface) => {
  console.log('编辑', item)
  supportFeeAddOrEditFeeDialog.value?.show(item)
}
onMounted(() => {
  getFeeInfoListHandle()
})
</script>

<style scoped lang="scss">
.card-header {
  font-size: 18px;
  font-weight: bold;
}

.text-center {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  text-align: center;
}

.container:not(:first-child) {
  margin-top: 20px;
}

.item-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  padding: 10px 15px;
  background-color: #efefef;
  border-radius: 20px;

  .item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
}
</style>
