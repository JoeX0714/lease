<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :destroy-on-close="true"
    width="500px"
  >
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="postData"
    >
      <el-form-item label="岗位名称" prop="name">
        <el-input
          v-model.trim="postData.name"
          placeholder="请填写岗位名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="岗位编码" prop="postCode">
        <el-input
          v-model.trim="postData.postCode"
          placeholder="请填写岗位编码"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="岗位描述" prop="description">
        <el-input
          v-model.trim="postData.description"
          placeholder="请填写岗位描述"
          clearable
          type="textarea"
        ></el-input>
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="postData.status">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button
        type="primary"
        @click="handleSubmit(ruleFormRef)"
        :loading="loading"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import { PostInterfacesRes } from '@/api/system/types'
interface DialogProps {
  rowData?: PostInterfacesRes
  api?: (params: any) => Promise<any>
  getTableList?: () => Promise<any>
}

let postData = ref<PostInterfacesRes>({} as PostInterfacesRes)
const rules = reactive({
  name: [{ required: true, message: '请填写岗位名称' }],
  postCode: [{ required: true, message: '请填写岗位编码' }],
  description: [{ required: true, message: '请填写岗位描述' }],
})

const dialogVisible = ref(false)
// dialog标题
const title = ref<string>()
const loading = ref<boolean>(false)

// props定义
const dialogProps = ref<DialogProps>()
// 接收父组件参数
const acceptParams = (params: DialogProps): void => {
  const row: any = params.rowData
  postData.value = row
  title.value = getDialogTitle(params)
  dialogProps.value = params
  dialogVisible.value = true
}
// 初始化dialog标题
const getDialogTitle = (params: DialogProps): string => {
  return params.rowData?.id ? '编辑' : '新增'
}
const ruleFormRef = ref<FormInstance>()

const handleSubmit = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      loading.value = true
      await dialogProps.value?.api!(postData.value)
      ElMessage.success({ message: `${title.value}成功！` })
      dialogProps.value?.getTableList!()
      dialogVisible.value = false
      loading.value = false
      // 重置表单
      resetForm(formEl)
    } catch (error) {
      loading.value = false
      console.log(error)
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
// 暴露给父组件的方法
defineExpose({
  acceptParams,
})
</script>

<style lang="scss" scoped></style>
