<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>租期管理</span>
      </div>
    </template>
    <el-row class="container">
      <el-col :span="2" class="text-center">租期</el-col>
      <el-col :span="22" class="item-container">
        <el-popconfirm
          v-for="item in termTypeList"
          :key="item.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          @confirm="deleteTermHandle(item)"
          @cancel="editTermHandle(item)"
          :title="`修改或删除${item.monthCount + item.unit}`"
        >
          <template #reference>
            <div class="item m-r-10 pointer">
              <el-tag style="padding: 13px">
                {{ item.monthCount + item.unit }}
              </el-tag>
            </div>
          </template>
        </el-popconfirm>
        <el-icon
          class="item m-r-10 m-t-5 pointer"
          :size="35"
          color="#567722"
          @click="addTermHandle()"
        >
          <CirclePlus />
        </el-icon>
      </el-col>
    </el-row>
    <!--    租期信息修改弹窗管理-->
    <SupportTermDialog
      ref="supportTermDialog"
      :updateTerm="getTermInfoListHandle"
    ></SupportTermDialog>
  </el-card>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { TermInfoInterface } from '@/api/apartmentManagement/types'
import { deleteTermById, getTermList } from '@/api/apartmentManagement'
import { ButtonPermission } from '@/enums/constEnums'
import SupportTermDialog from '@/views/apartmentManagement/attributeManagement/components/SupportTermDialog.vue'
import { ElMessage } from 'element-plus'
const supportTermDialog = ref<InstanceType<typeof SupportTermDialog>>()
// 租期类型
const termTypeList = ref<TermInfoInterface[]>([])
// 获取租期信息
async function getTermInfoListHandle() {
  try {
    const { data } = await getTermList()
    termTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 删除租期
const deleteTermHandle = async (item: TermInfoInterface) => {
  console.log('删除租期', item)
  try {
    await deleteTermById(item.id)
    await getTermInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
// 编辑租期
const editTermHandle = (item: TermInfoInterface) => {
  console.log('编辑租期', item)
  supportTermDialog.value?.show(item)
}
// 添加租期
const addTermHandle = () => {
  console.log('添加租期')
  supportTermDialog.value?.show()
}
onMounted(() => {
  getTermInfoListHandle()
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
