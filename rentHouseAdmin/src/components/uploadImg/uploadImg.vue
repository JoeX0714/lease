<template>
  <el-upload
    v-bind="$attrs"
    :action="UPLOAD_IMG_URL"
    :on-preview="handlePictureCardPreview"
    :headers="{ 'access-token': useUserStore().token }"
  >
    <el-icon>
      <Plus />
    </el-icon>
  </el-upload>

  <el-dialog v-model="dialogVisible">
    <el-image :src="dialogImageUrl" fit="fill" />
  </el-dialog>
</template>
<script setup lang="ts">
import { UPLOAD_IMG_URL } from '@/api/upload'
import { ref } from 'vue'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'
import { useUserStore } from '@/store/modules/user'

const dialogImageUrl = ref('')
const dialogVisible = ref(false)
function handlePictureCardPreview(uploadFile: UploadFile) {
  dialogImageUrl.value = uploadFile.url!
  dialogVisible.value = true
}
</script>

<style scoped lang="scss"></style>
