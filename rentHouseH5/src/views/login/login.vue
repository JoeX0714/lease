<template>
  <div class="main-container h-[100vh]">
    <div class="h-[30vh] flex flex-col justify-center items-center">
      <van-image round width="30vw" height="30vw" :src="defaultAvatarUrl" />
    </div>
    <div>
      <van-form ref="formRef">
        <van-cell-group inset>
          <!--            手机号-->
          <van-field
            v-model.trim="loginInfo.phone"
            border
            name="phone"
            required
            type="tel"
            autocomplete="off"
            placeholder="请输入手机号"
            :rules="[
              {
                required: true,
                pattern: /^1([3589]\d|4[5-9]|6[1-2,4-7]|7[0-8])\d{8}$/,
                message: '请正确填写手机号'
              }
            ]"
          />

          <van-field
            v-model.trim="loginInfo.code"
            name="code"
            placeholder="请输入验证码"
            clearable
            type="digit"
            maxlength="6"
            autocomplete="off"
            :rules="[
              {
                required: true,
                pattern: /^\d{4}$/,
                message: '请正确填写验证码'
              }
            ]"
          >
            <template #button>
              <van-button @click="getCodeHandle" size="small" type="primary">
                <div class="flex justify-center items-center">
                  <span class="--van-gray-1">{{
                    codeSendStatus ? "已发送" : "发送验证码"
                  }}</span>
                  <van-count-down
                    v-show="codeSendStatus"
                    ref="countDownRef"
                    @finish="countDownFinishHandle"
                    :time="countDown"
                    :auto-start="false"
                    format="ss"
                  >
                    <template #default="{ seconds }">
                      <span class="--van-gray-1">{{ `(${seconds}s)` }}</span>
                    </template>
                  </van-count-down>
                </div>
              </van-button>
            </template>
          </van-field>
        </van-cell-group>
        <div class="mt-[50px]">
          <loading-button
            round
            block
            type="primary"
            native-type="submit"
            :loadingClick="onSubmitHandle"
          >
            登录
          </loading-button>
        </div>
      </van-form>
    </div>
  </div>
</template>
<script setup lang="ts">
import defaultAvatarUrl from "../../../public/favicon.ico";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getSmsCode } from "@/api/user";
import type { CountDownInstance, FormInstance } from "vant";
import { useUserStore } from "@/store/modules/user";
import LoadingButton from "@/components/LoadingButton/LoadingButton.vue";
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
// 登录信息
const loginInfo = ref({
  phone: "",
  code: ""
});
// 表单实例
const formRef = ref<FormInstance>();
// 验证码发送状态
const codeSendStatus = ref(false);
//#region <倒计时相关>
// 倒计时
const countDown = ref(60 * 1000);
const countDownRef = ref<CountDownInstance>();
// 倒计时开始
const countDownStartHandle = () => {
  countDownRef.value?.start();
  //   修改验证码发送状态
  codeSendStatus.value = true;
};
// 倒计时重置
const countDownResetHandle = () => {
  countDownRef.value?.reset();
  //   修改验证码发送状态
  codeSendStatus.value = false;
};
// 倒计时结束
const countDownFinishHandle = () => {
  countDownResetHandle();
};
//#endregion
// 获取验证码
const getCodeHandle = async () => {
  // 验证字段phone
  await formRef.value?.validate("phone");
  // 开始倒计时
  countDownStartHandle();
  getSmsCode({ phone: loginInfo.value.phone });
};
const onSubmitHandle = async () => {
  console.log("onSubmit");
  // 验证字段
  await formRef.value?.validate();
  // 登录
  await userStore.LoginAction(loginInfo.value);
  // 跳转首页
  await router.replace(
    route.query?.redirect
      ? decodeURIComponent(route.query?.redirect as string)
      : "/"
  );
};
onMounted(() => {
  console.log("route", route);
  console.log("router-onMounted", router);
});
</script>

<style scoped lang="less"></style>
