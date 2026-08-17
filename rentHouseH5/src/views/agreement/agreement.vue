<template>
  <van-skeleton :row="20" :loading="!agreementDetailInfo?.roomId">
    <div class="page-container min-h-[100vh] py-[15px]">
      <!--    签约公寓-->
      <div>
        <div class="base-info-title main-container py-[4px]">签约公寓</div>
        <div class="my-[5px] px-[20px]">
          <van-card @click="goApartmentDetail()" class="rounded-xl shadow">
            <!--      title-->
            <template #title>
              <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                {{ `${agreementDetailInfo.apartmentName}` }}
              </h2>
            </template>
            <!--    thumb-->
            <template #thumb>
              <van-image
                class="w-full h-full object-cover"
                :src="
                  agreementDetailInfo.apartmentGraphVoList?.[0]?.url || '失败'
                "
              >
                <template v-slot:error>加载失败</template>
                <template v-slot:loading>
                  <van-loading type="spinner" size="20" />
                </template>
              </van-image>
            </template>
          </van-card>
        </div>
      </div>
      <!--    签约房间-->
      <div>
        <div class="base-info-title main-container py-[4px]">签约房间</div>
        <div class="my-[5px] px-[20px]">
          <van-card @click="goRoomDetail()" class="rounded-xl shadow">
            <!--      title-->
            <template #title>
              <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                {{ `${agreementDetailInfo.roomNumber}房间` }}
              </h2>
            </template>
            <!--    thumb-->
            <template #thumb>
              <van-image
                class="w-full h-full object-cover"
                :src="agreementDetailInfo.roomGraphVoList?.[0]?.url || '失败'"
              >
                <template v-slot:error>加载失败</template>
                <template v-slot:loading>
                  <van-loading type="spinner" size="20" />
                </template>
              </van-image>
            </template>
          </van-card>
        </div>
      </div>
      <!--    承租人信息-->
      <div>
        <div class="base-info-title main-container py-[4px]">签约房间</div>
        <div class="main-container my-[5px]">
          <div class="rounded-xl shadow overflow-hidden">
            <van-row>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="姓名"
                  v-model="agreementDetailInfo.name"
                ></van-field>
              </van-col>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="手机号"
                  v-model="agreementDetailInfo.phone"
                ></van-field>
              </van-col>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="身份证号"
                  v-model="agreementDetailInfo.identificationNumber"
                ></van-field>
              </van-col>
            </van-row>
          </div>
        </div>
      </div>
      <!--    租约详情-->
      <div>
        <div class="base-info-title main-container py-[4px]">租约详情</div>
        <div class="main-container my-[5px]">
          <div class="rounded-xl shadow overflow-hidden">
            <van-row>
              <!--          租期-->
              <van-col span="24">
                <van-field
                  v-model="leaseTermInfo.text"
                  is-link
                  readonly
                  name="picker"
                  label-width="70px"
                  label="租期"
                  placeholder="点击选择租期"
                  @click="isAllowEdit && (showPickerLeaseTerm = true)"
                />
                <van-popup v-model:show="showPickerLeaseTerm" position="bottom">
                  <van-picker
                    title="租期"
                    :columns="leaseTermColumns"
                    @confirm="onConfirmLeaseTerm"
                    @cancel="showPickerLeaseTerm = false"
                  />
                </van-popup>
              </van-col>
              <!--          合同租期-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="合同租期"
                  v-model="leaseDate"
                ></van-field>
              </van-col>
              <!--          身份证号-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="身份证号"
                  v-model="agreementDetailInfo.identificationNumber"
                ></van-field>
              </van-col>
              <!--          租金-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="租金"
                  v-model="rentText"
                ></van-field>
              </van-col>
              <!--          押金-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="押金(元)"
                  v-model="depositText"
                ></van-field>
              </van-col>
              <!--          支付方式-->
              <van-col span="24">
                <van-field
                  v-model="paymentTypeInfo.text"
                  is-link
                  readonly
                  name="picker"
                  label-width="70px"
                  label="支付方式"
                  placeholder="点击选择支付方式"
                  @click="isAllowEdit && (showPickerPaymentType = true)"
                />
                <van-popup
                  v-model:show="showPickerPaymentType"
                  position="bottom"
                >
                  <van-picker
                    title="支付方式"
                    :columns="paymentTypeColumns"
                    @confirm="onConfirmPaymentType"
                    @cancel="showPickerPaymentType = false"
                  />
                </van-popup>
              </van-col>
            </van-row>
          </div>
        </div>
      </div>
      <!--    按钮-->
      <div class="main-container py-[20px]">
        <van-button
          v-if="isAllowEdit && isRenew"
          type="primary"
          block
          round
          class="m-t-20"
          @click="submitHandle"
        >
          {{ isAddRenew ? "确认续约" : "保存" }}
        </van-button>
        <!--      确认签约-->
        <van-button
          v-if="loadedAgreementStatus === AgreementStatus.WAITING"
          type="success"
          block
          round
          class="m-t-20"
          @click="confirmSignHandle"
        >
          确认签约
        </van-button>
      </div>
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import dayjs from "dayjs";
import type {
  AgreementDetailInterface,
  PaymentInfoInterface,
  TermInfoInterface
} from "@/api/search/types";
import { useRoute, useRouter } from "vue-router";
import { showToast } from "vant";
import {
  getAgreementDetailById,
  getPaymentListByRoomId,
  getTermListByRoomId,
  saveOrUpdateAgreement
} from "@/api/search";
import { AgreementSource, AgreementStatus } from "@/enums/constEnums";
const route = useRoute();
const router = useRouter();
// 是否允许修改
const isAllowEdit = ref(route.query.isEdit === "true");
// 是否是新增续约
const isRenew = ref(route.query.isRenew === "true");

const isAddRenew = ref(route.query.isAdd === "true");
// 是否确认签约
const isConfirmAgreement = ref(route.query.isConfirm === "true");
// 详情加载时的租约状态（用于按状态控制"确认签约"按钮显隐）
const loadedAgreementStatus = ref<AgreementStatus>();
// 公寓的详情信息
const agreementDetailInfo = ref<Partial<AgreementDetailInterface>>({
  id: "",
  // 	承租人姓名
  name: "",
  // phone	承租人手机号码
  phone: "",
  // identificationNumber	承租人身份证号码
  identificationNumber: "",
  // apartmentId	签约公寓id
  apartmentId: "",
  // roomId	签约房间id
  roomId: "",
  // leaseStartDate	租约开始日期
  leaseStartDate: "",
  // leaseEndDate	租约结束日期
  leaseEndDate: "",
  // leaseTermId	租期id
  leaseTermId: "",
  // rent	租金（元/月）
  rent: "",
  // deposit	押金（元）
  deposit: "",
  // paymentTypeId	支付类型id
  paymentTypeId: "",
  // status	租约状态,可用值:1,2,3,4,5,6
  status: AgreementStatus.WAITING,
  // sourceType	租约来源,可用值:1,2
  sourceType: AgreementSource.NEW,
  // additionalInfo	备注信息
  additionalInfo: ""
});
//#region <租期相关>
// 合同租期
const leaseDate = computed(() => {
  return `${dayjs(agreementDetailInfo.value.leaseStartDate).format(
    "YYYY-MM-DD"
  )} 至 ${dayjs(agreementDetailInfo.value.leaseEndDate).format("YYYY-MM-DD")}`;
});
// 租期
const leaseTermInfo = ref({
  text: "",
  value: ""
});
// 监视leaseTermInfo的变化
watch(
  leaseTermInfo,
  newValue => {
    if (newValue) {
      console.log("newValue", newValue);
      // 重赋值租期
      agreementDetailInfo.value.leaseTermId = newValue.value;
      agreementDetailInfo.value.leaseTermMonthCount = +newValue.text.slice(
        0,
        -1
      );
      agreementDetailInfo.value.leaseTermUnit = newValue.text.slice(-1);
      //   重新计算起止日期
      agreementDetailInfo.value.leaseEndDate = dayjs(
        agreementDetailInfo.value.leaseStartDate
      )
        .add(agreementDetailInfo.value.leaseTermMonthCount as number, "month")
        .format("YYYY-MM-DD");
    }
  },
  { immediate: true, deep: true }
);
// 租期的列表
const leaseTermList = ref<TermInfoInterface[]>([]);
// 租期的列表的列
const showPickerLeaseTerm = ref(false);
const leaseTermColumns = computed(() => {
  return leaseTermList.value.map(item => ({
    text: item.monthCount + item.unit,
    value: item.id
  }));
});
// 获取租期列表
const getLeaseTermListHandle = async () => {
  const { data } = await getTermListByRoomId(
    agreementDetailInfo.value.roomId as string
  );
  leaseTermList.value = data;
};
// 确认选择租期
const onConfirmLeaseTerm = (data: {
  selectedOptions: (typeof leaseTermInfo.value)[];
}) => {
  console.log("onConfirmLeaseTerm", data);
  leaseTermInfo.value = data.selectedOptions[0];
  showPickerLeaseTerm.value = false;
};
//#endregion
//#region <支付方式相关>
// 支付方式的列表
const paymentTypeList = ref<PaymentInfoInterface[]>([]);
// 支付方式
const paymentTypeInfo = ref({
  text: "",
  value: ""
});
// 监视paymentTypeInfo的变化
watch(
  paymentTypeInfo,
  newValue => {
    if (newValue) {
      console.log("newValue", newValue);
      // 重赋值支付方式
      agreementDetailInfo.value.paymentTypeId = newValue.value;
      agreementDetailInfo.value.paymentTypeName = newValue.text;
    }
  },
  { immediate: true, deep: true }
);
// 支付方式的列表的列
const showPickerPaymentType = ref(false);
const paymentTypeColumns = computed(() => {
  return paymentTypeList.value.map(item => ({
    text: `${item.name}`,
    value: item.id
  }));
});
// 获取支付方式列表
const getPaymentTypeListHandle = async () => {
  const { data } = await getPaymentListByRoomId(
    agreementDetailInfo.value.roomId as string
  );
  paymentTypeList.value = data;
};
// 确认选择支付方式
const onConfirmPaymentType = (data: {
  selectedOptions: (typeof paymentTypeInfo.value)[];
}) => {
  console.log("onConfirmPaymentType", data);
  paymentTypeInfo.value = data.selectedOptions[0];
  showPickerPaymentType.value = false;
};
//#endregion
//#region <租金、押金相关>
// 租金
const rentText = computed(() => {
  return `${agreementDetailInfo.value.rent}元/月`;
});
// 押金
const depositText = computed(() => {
  return `${agreementDetailInfo.value.deposit}元`;
});
//#endregion
// 获取公寓的详情信息
const getAgreementDetailHandle = async () => {
  const { data } = await getAgreementDetailById(route.query.id as string);
  agreementDetailInfo.value = data;
  loadedAgreementStatus.value = data.status;
  // 单独租期相关
  leaseTermInfo.value.text = data.leaseTermMonthCount + data.leaseTermUnit;
  leaseTermInfo.value.value = data.leaseTermId as string;
  // 单独支付方式相关
  paymentTypeInfo.value.text = data.paymentTypeName as string;
  paymentTypeInfo.value.value = data.paymentTypeId as string;
  //   如果是新增续约，允许修改并重新计算起止日期
  if (isAllowEdit.value && isAddRenew.value) {
    // 重新计算起止日期
    // 结束日期变成开始日期
    agreementDetailInfo.value.leaseStartDate =
      agreementDetailInfo.value.leaseEndDate;
    // 结束日期变成开始日期+租期
    agreementDetailInfo.value.leaseEndDate = dayjs(
      agreementDetailInfo.value.leaseStartDate
    )
      .add(agreementDetailInfo.value.leaseTermMonthCount as number, "month")
      .format("YYYY-MM-DD");
    //   重置租约状态
    agreementDetailInfo.value.status = AgreementStatus.RENEW_TO_BE_CONFIRMED;
    //   重置租约来源
    agreementDetailInfo.value.sourceType = AgreementSource.RENEW;
  }
  //   如果是新增的续约,重置id为空
  isAddRenew.value && (agreementDetailInfo.value.id = "");
  //   如果是确认签约，修改状态为已签约，等待确认
  isConfirmAgreement.value &&
    (agreementDetailInfo.value.status = AgreementStatus.SIGNED);
};
// 更正租期和支付方式
const correctLeaseTermAndPaymentType = () => {
  // 修正租期，如果不存在就改为数组第一项
  let targetTerm =
    leaseTermList.value.find(
      item => item.id === agreementDetailInfo.value.leaseTermId
    ) || leaseTermList.value[0];
  if (targetTerm) {
    console.log("targetTerm", targetTerm);
    leaseTermInfo.value.text = targetTerm.monthCount + targetTerm.unit;
    leaseTermInfo.value.value = targetTerm.id as unknown as string;
  }
  //   修正支付方式，如果不存在就改为数组第一项
  let targetPaymentType =
    paymentTypeList.value.find(
      item => item.id === agreementDetailInfo.value.paymentTypeId
    ) || paymentTypeList.value[0];
  if (targetPaymentType) {
    console.log("targetPaymentType", targetPaymentType);
    paymentTypeInfo.value.text = targetPaymentType.name as string;
    paymentTypeInfo.value.value = targetPaymentType.id as unknown as string;
  }
};
// 跳转到公寓的详情页面
const goApartmentDetail = () => {
  router.push({
    path: "/apartmentDetail",
    query: { id: agreementDetailInfo.value.apartmentId }
  });
};
// 跳转到房间的详情页面
const goRoomDetail = () => {
  router.push({
    path: "/roomDetail",
    query: { id: agreementDetailInfo.value.roomId }
  });
};
// 确认签约（仅签约待确认状态的租约显示该按钮）
async function confirmSignHandle() {
  try {
    // 复用现有确认流程的租期/支付方式校验
    let leaseTermMonthCount =
      leaseTermList.value.find(
        item => item.id === agreementDetailInfo.value.leaseTermId
      )?.monthCount || 0;
    let paymentTypeMonthCount =
      paymentTypeList.value.find(
        item => item.id === agreementDetailInfo.value.paymentTypeId
      )?.payMonthCount || 0;
    if (+leaseTermMonthCount < +paymentTypeMonthCount) {
      showToast({
        type: "fail",
        message: "租期时长不能小于支付方式月份长度"
      });
      return;
    }
    agreementDetailInfo.value.status = AgreementStatus.SIGNED;
    await saveOrUpdateAgreement(agreementDetailInfo.value);
    showToast({ type: "success", message: "操作成功" });
    await getAgreementDetailHandle();
  } catch (error) {
    console.log(error);
  }
}
// 续约,修改
const submitHandle = async () => {
  console.log("确认签约");
  try {
    // 租期时长不能小于支付方式月份长度
    let leaseTermMonthCount =
      leaseTermList.value.find(
        item => item.id === agreementDetailInfo.value.leaseTermId
      )?.monthCount || 0;

    console.log(leaseTermList.value);
    console.log(agreementDetailInfo.value);
    console.log("=============================");
    console.log("租期长度：" + leaseTermMonthCount);

    let paymentTypeMonthCount =
      paymentTypeList.value.find(
        item => item.id === agreementDetailInfo.value.paymentTypeId
      )?.payMonthCount || 0;

    console.log("支付月份长度：" + paymentTypeMonthCount);

    if (+leaseTermMonthCount < +paymentTypeMonthCount) {
      showToast({
        type: "fail",
        message: "租期时长不能小于支付方式月份长度"
      });
      return;
    }

    await saveOrUpdateAgreement(agreementDetailInfo.value);
    showToast({ type: "success", message: "操作成功" });
    router.back();
  } catch (error) {
    console.log(error);
  }
};
onMounted(async () => {
  route.query.id && (await getAgreementDetailHandle());
  await getLeaseTermListHandle();
  await getPaymentTypeListHandle();
  //  如果允许修改，修正租期和支付方式
  isAllowEdit.value && correctLeaseTermAndPaymentType();
});
</script>

<style scoped lang="less">
.base-info-title {
  //background-color: var(--van-primary-background-color);
  font-weight: bold;
  //color: white;
}
::v-deep .van-card {
  background: var(--van-background-2) !important;
}
</style>
