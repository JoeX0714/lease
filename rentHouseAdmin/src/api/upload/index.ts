// 上传基础路径
export const BASE_URL = import.meta.env.PROD
  ? import.meta.env.VITE_APP_BASE_URL
  : ''
// 图片上传地址
export const UPLOAD_IMG_URL = `${BASE_URL}/admin/file/upload`
