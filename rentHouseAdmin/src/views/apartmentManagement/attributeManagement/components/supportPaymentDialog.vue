<template>
  <el-dialog
    v-model="dialogFormVisible"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 700px"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="支付方式名称" prop="name">
        <el-input
          v-model.trim="formData.name"
          autocomplete="off"
          placeholder="请输入，例：月付"
        />
      </el-form-item>
      <el-form-item label="每次支付月数" prop="payMonthCount">
        <el-input-number
          v-model.trim="formData.payMonthCount"
          :min="1"
          step-strictly
          placeholder="请输入，例：3"
        />
      </el-form-item>
      <el-form-item label="支付方式说明" prop="additionalInfo">
        <el-input
          v-model.trim="formData.additionalInfo"
          autocomplete="off"
          placeholder="请输入，例：押一付一"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { PaymentInfoInterface } from '@/api/apartmentManagement/types'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { saveOrUpdatePayment } from '@/api/apartmentManagement'
const props = defineProps({
  update: {
    type: Function,
    default: () => ({}),
  },
})
const defaultFormData = {
  id: '',
  name: '',
  payMonthCount: 1,
  additionalInfo: '',
}
const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<PaymentInfoInterface>({
  ...defaultFormData,
})
// 表单验证规则
const rules = ref<FormRules>({
  name: [{ required: true, message: '支付方式名称', trigger: 'blur' }],
  payMonthCount: [
    { required: true, message: '请输入每次支付月数', trigger: 'blur' },
  ],
  additionalInfo: [
    { required: true, message: '请输入支付方式说明', trigger: 'blur' },
  ],
})
const title = computed(() => {
  return (formData.value.id ? '修改' : '新增') + '支付方式'
})
// 展示方法
const show = (data: Partial<PaymentInfoInterface> = defaultFormData) => {
  console.log('show', data)
  formData.value = Object.assign({}, defaultFormData, data)
  dialogFormVisible.value = true
}
// 关闭方法
const close = () => {
  dialogFormVisible.value = false
}

// 提交方法
const submitHandle = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      await saveOrUpdatePayment(formData.value)
      await props.update()
      ElMessage.success('操作成功')
      close()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}
// 对外暴露
defineExpose({
  show,
  close,
})
</script>

<style scoped lang="scss">
.icon-option {
  display: flex;
  align-items: center;
}
</style>
