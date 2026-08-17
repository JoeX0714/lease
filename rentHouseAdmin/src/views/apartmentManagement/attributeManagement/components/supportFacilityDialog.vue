<template>
  <el-dialog
    v-model="dialogFormVisible"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 700px"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="配套名称" prop="name">
        <el-input
          v-model.trim="formData.name"
          autocomplete="off"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item label="配套图标" prop="icon">
        <el-select
          v-model="formData.icon"
          placeholder="请选择配套图标"
          clearable
          filterable
        >
          <el-option
            class="icon-option"
            v-for="item in icons"
            :key="item"
            :value="item"
          >
            <SvgIcon size="25" color="black" :name="`rent-${item}`" />
            <span class="m-l-10">{{ item }}</span>
          </el-option>
        </el-select>
        <SvgIcon
          v-if="formData.icon"
          size="25"
          class="m-l-10"
          color="black"
          :name="`rent-${formData.icon}`"
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
import ids from 'virtual:svg-icons-names'
import { computed, ref } from 'vue'
import { FacilityInfoInterface } from '@/api/apartmentManagement/types'
import { BuildingTypeTypeMap, getLabelByValue } from '@/enums/constEnums'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { saveOrUpdateFacilityInfo } from '@/api/apartmentManagement'
const props = defineProps({
  updateFacility: {
    type: Function,
    default: () => ({}),
  },
})
const defaultFormData = {
  id: '',
  type: '',
  name: '',
  icon: '',
}
const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<FacilityInfoInterface>({
  ...defaultFormData,
})
// 表单验证规则
const rules = ref<FormRules>({
  name: [{ required: true, message: '请输入配套名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请选择配套图标', trigger: 'change' }],
})
const title = computed(() => {
  return (
    (formData.value.id ? '修改' : '新增') +
    getLabelByValue(BuildingTypeTypeMap, formData.value.type) +
    '配套'
  )
})
// 过滤出所有的配套图标
const icons = ids
  .filter((id) => id.startsWith('icon-rent-'))
  .map((id) => id.replace('icon-rent-', ''))
// 展示方法
const show = (data: Partial<FacilityInfoInterface> = defaultFormData) => {
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
      await saveOrUpdateFacilityInfo(formData.value)
      await props.updateFacility()
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
