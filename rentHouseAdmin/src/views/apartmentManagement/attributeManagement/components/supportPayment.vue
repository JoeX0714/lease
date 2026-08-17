<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>支付方式管理</span>
      </div>
    </template>
    <el-row class="container">
      <el-col :span="2" class="text-center">支付方式</el-col>
      <el-col :span="22" class="item-container">
        <el-popconfirm
          v-for="item in paymentTypeList"
          :key="item.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          @confirm="deleteHandle(item)"
          @cancel="editHandle(item)"
          :title="`修改或删除${item.name}(${item.additionalInfo})`"
        >
          <template #reference>
            <div class="item m-r-10 pointer">
              <el-tag style="padding: 13px">
                {{ `${item.name}(${item.additionalInfo})` }}
              </el-tag>
            </div>
          </template>
        </el-popconfirm>
        <el-icon
          class="item m-r-10 m-t-5 pointer"
          :size="35"
          color="#567722"
          @click="addHandle()"
        >
          <CirclePlus />
        </el-icon>
      </el-col>
    </el-row>
    <!--    支付方式信息修改弹窗管理-->
    <SupportPaymentDialog
      ref="supportPaymentDialog"
      :update="getPaymentInfoListHandle"
    ></SupportPaymentDialog>
  </el-card>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { PaymentInfoInterface } from '@/api/apartmentManagement/types'
import { deletePaymentById, getPaymentList } from '@/api/apartmentManagement'
import { ButtonPermission } from '@/enums/constEnums'
import SupportPaymentDialog from '@/views/apartmentManagement/attributeManagement/components/SupportPaymentDialog.vue'
import { ElMessage } from 'element-plus'
const supportPaymentDialog = ref<InstanceType<typeof SupportPaymentDialog>>()
// 支付方式类型
const paymentTypeList = ref<PaymentInfoInterface[]>([])
// 获取支付方式信息
async function getPaymentInfoListHandle() {
  try {
    const { data } = await getPaymentList()
    paymentTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 删除支付方式
const deleteHandle = async (item: PaymentInfoInterface) => {
  console.log('删除支付方式', item)
  try {
    await deletePaymentById(item.id)
    await getPaymentInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}
// 编辑支付方式
const editHandle = (item: PaymentInfoInterface) => {
  console.log('编辑支付方式', item)
  supportPaymentDialog.value?.show(item)
}
// 添加支付方式
const addHandle = () => {
  console.log('添加支付方式')
  supportPaymentDialog.value?.show()
}
onMounted(() => {
  getPaymentInfoListHandle()
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
