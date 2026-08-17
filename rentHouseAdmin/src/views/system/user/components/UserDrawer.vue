<template>
  <el-drawer
    v-model="drawerVisible"
    :destroy-on-close="true"
    size="450px"
    :title="`${drawerProps.title}用户`"
  >
    <el-form
      ref="ruleFormRef"
      label-width="120px"
      label-suffix=" :"
      :rules="rules"
      :model="drawerProps.rowData"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          :disabled="drawerProps.title === '分配角色'"
          v-model.trim="drawerProps.rowData!.username"
          placeholder="请填写用户姓名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item
        :label="drawerProps.title === '编辑' ? '修改用户密码' : '用户密码'"
        prop="password"
        v-if="drawerProps.title === '新增' || drawerProps.title === '编辑'"
      >
        <el-input
          v-model.trim="drawerProps.rowData!.password"
          placeholder="请填写用户密码"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item
        label="确认密码"
        prop="confirmPassword"
        v-if="drawerProps.title === '编辑'"
      >
        <el-input
          v-model.trim="drawerProps.rowData!.confirmPassword"
          placeholder="请再次填写用户密码"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item
        label="用户昵称"
        prop="name"
        v-if="drawerProps.title !== '分配角色'"
      >
        <el-input
          v-model.trim="drawerProps.rowData!.name"
          placeholder="请填写用户昵称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item
        label="手机"
        prop="phone"
        v-if="drawerProps.title !== '分配角色'"
      >
        <el-input
          v-model.trim="drawerProps.rowData!.phone"
          placeholder="请填写用户手机"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item
        label="岗位"
        prop="postId"
        v-if="drawerProps.title !== '分配角色'"
      >
        <el-select
          v-model="drawerProps.rowData!.postId"
          placeholder="请选择岗位"
          clearable
          style="width: 95%"
        >
          <el-option
            v-for="item in drawerProps.postList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="用户类型"
        prop="type"
        v-if="drawerProps.title !== '分配角色'"
      >
        <el-radio-group v-model="drawerProps.rowData!.type">
          <el-radio
            :label="item.value"
            v-for="item in SystemUserTypeMap"
            :key="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <!--      分配角色-->
      <el-form-item
        label="角色列表"
        prop="rolesId"
        v-if="drawerProps.title === '分配角色'"
      >
        <el-checkbox
          v-model="state.checkAll"
          :indeterminate="state.isIndeterminate"
          @change="handleCheckAllChange"
        >
          全选
        </el-checkbox>
        <el-checkbox-group
          v-model="state.assginRoleList"
          @change="handleCheckedChange"
        >
          <el-checkbox
            v-for="item in state.allRolesList"
            :key="item.id"
            :label="item.id"
          >
            {{ item.name }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        确定
      </el-button>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import {
  CheckboxValueType,
  ElMessage,
  FormInstance,
  FormRules,
} from 'element-plus'
import { DeptInterfacesRes, PostInterfacesRes, Role } from '@/api/system/types'
import { SystemUserTypeMap } from '@/enums/constEnums'
import { checkUserNameAvailable } from '@/api/system'
// 传入进来的最初的数据
const origiinRowData = ref<any>({})
interface DrawerProps {
  title: string
  rowData?: any
  list?: any
  api?: (params: any) => Promise<any>
  getTableList?: () => Promise<any>
  postList?: PostInterfacesRes[]
  deptList?: DeptInterfacesRes[]
}

interface RolesState {
  allRolesList: Role[]
  assginRoleList: string[] | number[]
  checkAll: boolean
  isIndeterminate: boolean
}

// drawer框状态
const drawerVisible = ref(false)
const drawerProps = ref<DrawerProps>({
  title: '',
})

const loading = ref<boolean>(false)
const checkUserName = async (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入用户名'))
  }
  if (value.length < 4) {
    return callback(new Error('用户名不能小于4位'))
  }
  if (value.length > 20) {
    return callback(new Error('用户名不能大于20位'))
  }
  // 新值和原始值相同，不需要校验
  if (value === origiinRowData.value.username) {
    return callback()
  }
  try {
    const { data } = await checkUserNameAvailable(value)
    if (!data) {
      return callback(new Error('用户名已存在'))
    }
    callback()
  } catch (error) {
    console.error(error)
    callback(new Error('用户名校验失败'))
  }
}
const checkConfirmPassword = async (rule: any, value: any, callback: any) => {
  // 新值和原始值相同，不需要校验
  if (value === drawerProps.value.rowData?.password) {
    return callback()
  } else {
    return callback(new Error('两次输入密码不一致'))
  }
}
const rules = reactive<FormRules>({
  username: [
    {
      required: true,
      validator: checkUserName as unknown as () => void,
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: true,
      message: '请填写用户密码',
      trigger: 'blur',
    },
    { min: 6, message: '密码不能小于6位' },
  ],
  confirmPassword: [
    {
      validator: checkConfirmPassword as unknown as () => void,
      trigger: 'blur',
    },
  ],
  name: [{ required: true, message: '请填写用户昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请填写用户手机', trigger: 'blur' }],
  postId: [{ required: true, message: '请选择所属岗位', trigger: 'change' }],
  type: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
})
watch(
  drawerProps,
  (newVal) => {
    if (newVal) {
      ;(rules as any).password[0].required = newVal.title === '新增'
    }
  },
  { immediate: true, deep: true },
)

// 角色选择状态管理
const state: RolesState = reactive({
  allRolesList: [],
  assginRoleList: [],
  checkAll: false,
  isIndeterminate: false,
})

// 全选
const handleCheckAllChange = (val: CheckboxValueType) => {
  state.assginRoleList = val
    ? state.allRolesList.map((item: Role) => item.id)
    : []
  console.log(state.assginRoleList)

  state.isIndeterminate = false
}

// 单选
const handleCheckedChange = (value: CheckboxValueType[]) => {
  const checkedCount = value.length
  state.checkAll = checkedCount === state.allRolesList.length
  state.isIndeterminate =
    checkedCount > 0 && checkedCount < state.allRolesList.length
}

// 接收父组件传过来的参数
const acceptParams = (params: DrawerProps): void => {
  // 保存初始值
  origiinRowData.value = JSON.parse(JSON.stringify(params.rowData))
  // 如果是编辑，需要把密码置空，新增确认密码字段
  if (params.title === '编辑') {
    params.rowData.password = ''
    params.rowData.confirmPassword = ''
  }
  if (params.title === '分配角色') {
    const { list } = params
    state.allRolesList = list.data
    state.assginRoleList = list?.data
      .filter((item: Role) => item.selected)
      .map((item: Role) => item.id)
    state.isIndeterminate = state.assginRoleList.length > 0 ? true : false
  }
  drawerProps.value = params
  drawerVisible.value = true
}

const ruleFormRef = ref<FormInstance>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    console.log('valid', valid)
    if (!valid) return
    try {
      loading.value = true
      if (drawerProps.value.title === '分配角色') {
        const params = {
          userId: drawerProps.value.rowData.id,
          roleIdList: state.assginRoleList,
        }
        await drawerProps.value.api!(params)
      } else {
        await drawerProps.value.api!(drawerProps.value.rowData)
      }
      ElMessage.success({ message: `${drawerProps.value.title}用户成功！` })
      drawerProps.value.getTableList!()
      drawerVisible.value = false
      loading.value = false
    } catch (error) {
      loading.value = false

      console.log(error)
    }
  })
}

defineExpose({
  acceptParams,
})
</script>
<style lang="scss" scoped></style>
