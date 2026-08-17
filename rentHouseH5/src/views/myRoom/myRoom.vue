<template>
  <div>
    <!--    顶部背景-->
    <van-image :src="bgImgUrl">
      <template v-slot:error>加载失败</template>
      <template v-slot:loading>
        <van-loading type="spinner" size="20" />
      </template>
    </van-image>
    <div v-if="displayAgreement" class="main-container pt-[20px] pb-[5px]">
      <div class="flex items-center justify-between gap-[15px]">
        <span class="text-[16px] font-bold">
          {{
            `${displayAgreement.apartmentName} ${displayAgreement.roomNumber}房间`
          }}
        </span>
        <van-tag
          :type="
            displayAgreement.leaseStatus === AgreementStatus.SIGNED
              ? 'success'
              : 'primary'
          "
          size="medium"
        >
          {{
            getLabelByValue(
              AgreementStatusMap,
              displayAgreement.leaseStatus
            )
          }}
        </van-tag>
      </div>
      <div class="mt-[8px] text-[14px] --van-gray-6">
        {{
          `${displayAgreement.leaseStartDate} 至 ${displayAgreement.leaseEndDate}`
        }}
      </div>
    </div>
    <!--    中间-->
    <div class="main-container flex justify-around mt-[15px]">
      <div
        v-for="item in navList"
        :key="item.name"
        class="flex flex-col justify-center items-center"
        :class="{ 'opacity-40': item.disabled }"
        @click="handleNavClick(item)"
      >
        <SvgIcon :name="item.icon" size="45" />
        <span>{{ item.name }}</span>
      </div>
    </div>

    <van-action-sheet
      v-model:show="agreementSelectVisible"
      :actions="agreementSelectActions"
      :title="selectAction === 'repair' ? '选择报修房间' : '选择退租房间'"
      cancel-text="取消"
      close-on-click-action
      @select="agreementSelectHandle"
    />

    <!--    报修弹窗-->
    <van-popup v-model:show="repairVisible" position="bottom" round>
      <div class="p-[15px]">
        <div class="text-[15px] font-bold mb-[10px]">
          报修：{{ selectedAgreement?.apartmentName }}
          {{ selectedAgreement?.roomNumber }}房间
        </div>
        <van-field
          v-model="repairContent"
          type="textarea"
          rows="3"
          maxlength="200"
          show-word-limit
          placeholder="请输入报修内容，例如：卫生间水龙头漏水"
        />
        <van-button
          type="primary"
          block
          round
          class="mt-[15px]"
          :loading="repairSubmitting"
          @click="submitRepairHandle"
        >
          提交
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts" name="MyRoom">
import { computed, onActivated, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import bgImgUrl from "@/assets/my_room_bg.png";
import {
  getMyAgreementList,
  saveOrUpdateAgreement
} from "@/api/search";
import { submitRepair } from "@/api/repair";
import type { AgreementItemInterface } from "@/api/search/types";
import {
  AgreementStatus,
  AgreementStatusMap,
  getLabelByValue
} from "@/enums/constEnums";
import { showConfirmDialog, showToast } from "vant";

const router = useRouter();
// 当前仍占用的全部租约：已签约(2)、退租审核中(5)
const occupiedAgreements = ref<AgreementItemInterface[]>([]);
const signedAgreements = computed(() =>
  occupiedAgreements.value.filter(
    item => item.leaseStatus === AgreementStatus.SIGNED
  )
);
const withdrawingAgreements = computed(() =>
  occupiedAgreements.value.filter(
    item => item.leaseStatus === AgreementStatus.TO_BE_CONFIRMED
  )
);
const displayAgreement = computed(() => {
  if (occupiedAgreements.value.length === 1) {
    return occupiedAgreements.value[0];
  }
  return (
    occupiedAgreements.value.find(
      item => item.leaseStatus === AgreementStatus.SIGNED
    ) ||
    occupiedAgreements.value.find(
      item => item.leaseStatus === AgreementStatus.TO_BE_CONFIRMED
    )
  );
});
// 提交防重复锁
const submitting = ref(false);

const withdrawText = computed(() => {
  return !signedAgreements.value.length && withdrawingAgreements.value.length
    ? "退租审核中"
    : "退租";
});

const navList = computed(() => [
  {
    icon: "物业费用出账",
    name: withdrawText.value,
    disabled: !signedAgreements.value.length,
    action: "withdraw"
  },
  {
    icon: "物业报修",
    name: "报修",
    disabled: false,
    action: "repair"
  },
  {
    icon: "物业报修",
    name: "我的报修",
    disabled: false,
    action: "myRepair"
  }
]);

async function getCurrentAgreementsHandle() {
  try {
    const { data } = await getMyAgreementList();
    occupiedAgreements.value = data
      .map(item => ({
        ...item,
        leaseStatus: Number(item.leaseStatus) as AgreementStatus
      }))
      .filter(
        item =>
          item.leaseStatus === AgreementStatus.SIGNED ||
          item.leaseStatus === AgreementStatus.TO_BE_CONFIRMED
      );
  } catch (error) {
    console.log(error);
  }
}

// 申请退租
async function handleWithdraw(agreement: AgreementItemInterface) {
  if (submitting.value) return;
  if (agreement.leaseStatus !== AgreementStatus.SIGNED) return;
  try {
    await showConfirmDialog({
      title: "申请退租",
      message: "确定要申请退租吗？",
      cancelButtonText: "取消",
      confirmButtonText: "确定"
    });
  } catch {
    return; // 用户取消
  }
  submitting.value = true;
  try {
    await saveOrUpdateAgreement({
      id: agreement.id,
      status: AgreementStatus.TO_BE_CONFIRMED
    });
    showToast({ type: "success", message: "操作成功" });
    // 立即更新本地状态，防止刷新延迟期间重复提交
    agreement.leaseStatus = AgreementStatus.TO_BE_CONFIRMED;
    await getCurrentAgreementsHandle();
  } catch (error) {
    console.log(error);
  } finally {
    submitting.value = false;
  }
}

// 报修相关
const repairVisible = ref(false);
const repairContent = ref("");
const repairSubmitting = ref(false);
const selectedAgreement = ref<AgreementItemInterface>();

type AgreementSelectType = "repair" | "withdraw";
type AgreementSelectAction = {
  name: string;
  subname: string;
  agreement: AgreementItemInterface;
};
const agreementSelectVisible = ref(false);
const selectAction = ref<AgreementSelectType>("repair");
const agreementSelectActions = computed<AgreementSelectAction[]>(() => {
  const agreements =
    selectAction.value === "repair"
      ? occupiedAgreements.value
      : signedAgreements.value;
  return agreements.map(agreement => ({
    name: `${agreement.apartmentName} ${agreement.roomNumber}房间`,
    subname: getLabelByValue(AgreementStatusMap, agreement.leaseStatus),
    agreement
  }));
});

function openRepairPopup(agreement: AgreementItemInterface) {
  selectedAgreement.value = agreement;
  repairContent.value = "";
  repairVisible.value = true;
}

function selectAgreementHandle(type: AgreementSelectType) {
  const agreements =
    type === "repair" ? occupiedAgreements.value : signedAgreements.value;
  if (!agreements.length) {
    if (type === "repair") showToast("暂无在租房间，无法报修");
    return;
  }
  if (agreements.length === 1) {
    const [agreement] = agreements;
    if (!agreement) return;
    type === "repair"
      ? openRepairPopup(agreement)
      : handleWithdraw(agreement);
    return;
  }
  selectAction.value = type;
  agreementSelectVisible.value = true;
}

function agreementSelectHandle(action: AgreementSelectAction) {
  agreementSelectVisible.value = false;
  selectAction.value === "repair"
    ? openRepairPopup(action.agreement)
    : handleWithdraw(action.agreement);
}

async function submitRepairHandle() {
  if (!repairContent.value.trim()) {
    showToast("请填写报修内容");
    return;
  }
  if (repairSubmitting.value) return;
  repairSubmitting.value = true;
  try {
    await submitRepair({
      agreementId: selectedAgreement.value?.id as number,
      roomId: selectedAgreement.value?.roomId as number,
      repairContent: repairContent.value.trim()
    });
    showToast({ type: "success", message: "报修提交成功" });
    repairVisible.value = false;
  } catch (error) {
    console.log(error);
  } finally {
    repairSubmitting.value = false;
  }
}

function handleNavClick(item: { action: string; disabled?: boolean }) {
  if (item.disabled) return;
  if (item.action === "withdraw") {
    selectAgreementHandle("withdraw");
  } else if (item.action === "repair") {
    selectAgreementHandle("repair");
  } else if (item.action === "myRepair") {
    router.push({ path: "/myRepair" });
  }
}

const initialized = ref(false);
onMounted(async () => {
  await getCurrentAgreementsHandle();
  initialized.value = true;
});
onActivated(() => {
  if (initialized.value) getCurrentAgreementsHandle();
});
</script>

<style lang="less" scoped></style>
