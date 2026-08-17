<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>房间基本信息管理</span>
      </div>
    </template>
    <el-row class="container" v-for="item in attrTypeList" :key="item">
      <el-col :span="2" class="text-center">
        <el-popconfirm
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          @confirm="deleteAttrHandle(item)"
          @cancel="editAttrHandle(item)"
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
          v-for="item2 in item.attrValueList || []"
          :key="item2.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          @confirm="deleteRoomBaseHandle(item2)"
          @cancel="editRoomBaseHandle(item2)"
          :title="`修改或删除${item2.name}`"
        >
          <template #reference>
            <div class="item m-r-10 pointer">
              <el-tag style="padding: 13px">{{ item2.name }}</el-tag>
            </div>
          </template>
        </el-popconfirm>
        <el-icon
          class="item m-r-10 m-t-5 pointer"
          :size="35"
          color="#567722"
          @click="addRoomBaseHandle(item)"
        >
          <CirclePlus />
        </el-icon>
      </el-col>
    </el-row>
    <el-button
      type="primary"
      class="m-t-20"
      @click="addAttrHandle"
    >
      添加新属性
    </el-button>
    <!--    信息修改弹窗管理-->
    <SupportRoomBaseDialog
      ref="supportRoomBaseDialog"
      :updateRoomBase="getRoomBaseInfoListHandle"
    ></SupportRoomBaseDialog>
    <!--    属性信息弹窗管理-->
    <supportRoomBaseAddOrEditAttrDialog
      ref="supportRoomBaseAddOrEditAttrDialog"
      :updateRoomBaseAddOrEditAttr="getRoomBaseInfoListHandle"
    ></supportRoomBaseAddOrEditAttrDialog>
  </el-card>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  AttrInfoInfoInterface,
  AttrValueInfoInterface,
} from '@/api/apartmentManagement/types'
import {
  deleteAttrKeyById,
  deleteAttrValueById,
  getAttrInfoList,
} from '@/api/apartmentManagement'
import SupportRoomBaseDialog from '@/views/apartmentManagement/attributeManagement/components/supportRoomBaseDialog.vue'
import { ElMessage } from 'element-plus'
import SupportRoomBaseAddOrEditAttrDialog from '@/views/apartmentManagement/attributeManagement/components/supportRoomBaseAddOrEditAttrDialog.vue'
import { ButtonPermission } from '@/enums/constEnums'
const supportRoomBaseDialog = ref<InstanceType<typeof SupportRoomBaseDialog>>()
const supportRoomBaseAddOrEditAttrDialog =
  ref<InstanceType<typeof SupportRoomBaseAddOrEditAttrDialog>>()

// 属性类型
const attrTypeList = ref<AttrInfoInfoInterface[]>([])
// 获取属性信息
async function getRoomBaseInfoListHandle() {
  try {
    const { data } = await getAttrInfoList()
    data.forEach((item) => {
      item.attrValueList?.forEach((item2) => {
        item2.attrKeyName = item.name
      })
    })
    attrTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 删除属性值
const deleteRoomBaseHandle = async (item: AttrValueInfoInterface) => {
  console.log('删除', item)
  try {
    await deleteAttrValueById(item.id)
    await getRoomBaseInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
// 编辑属性值
const editRoomBaseHandle = (item: AttrValueInfoInterface) => {
  console.log('编辑', item)
  supportRoomBaseDialog.value?.show(item)
}
// 添加属性值
const addRoomBaseHandle = (item: AttrInfoInfoInterface) => {
  console.log('添加属性', item)
  supportRoomBaseDialog.value?.show({
    attrKeyId: item.id,
    attrKeyName: item.name,
  })
}
// 添加新属性
const addAttrHandle = () => {
  supportRoomBaseAddOrEditAttrDialog.value?.show()
}
// 删除属性
const deleteAttrHandle = async (item: AttrInfoInfoInterface) => {
  console.log('删除', item)
  try {
    await deleteAttrKeyById(item.id)
    await getRoomBaseInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
const editAttrHandle = (item: AttrInfoInfoInterface) => {
  console.log('编辑', item)
  supportRoomBaseAddOrEditAttrDialog.value?.show(item)
}
onMounted(() => {
  getRoomBaseInfoListHandle()
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
