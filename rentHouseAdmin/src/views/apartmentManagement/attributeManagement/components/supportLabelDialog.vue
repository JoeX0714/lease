<template>
  <el-dialog
    v-model="dialogFormVisible"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 700px"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="标签名称" prop="name">
        <el-input
          v-model.trim="formData.name"
          autocomplete="off"
          placeholder="请输入"
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
import { LabelInfoInterface } from '@/api/apartmentManagement/types'
import { BuildingTypeTypeMap, getLabelByValue } from '@/enums/constEnums'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { saveOrUpdateLabelInfo } from '@/api/apartmentManagement'
const props = defineProps({
  updateLabel: {
    type: Function,
    default: () => ({}),
  },
})
const defaultFormData = {
  id: '',
  type: '',
  name: '',
}
const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<LabelInfoInterface>({
  ...defaultFormData,
})
// 表单验证规则
const rules = ref<FormRules>({
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
})
const title = computed(() => {
  return (
    (formData.value.id ? '修改' : '新增') +
    getLabelByValue(BuildingTypeTypeMap, formData.value.type) +
    '标签'
  )
})
// 展示方法
const show = (data: Partial<LabelInfoInterface> = defaultFormData) => {
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
      await saveOrUpdateLabelInfo(formData.value)
      await props.updateLabel()
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
