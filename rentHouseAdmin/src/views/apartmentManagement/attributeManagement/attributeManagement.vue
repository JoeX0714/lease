<template>
  <el-card>
    <!--    配套信息管理-->
    <SupportFacility></SupportFacility>
    <!--    标签信息管理-->
    <SupportLabel class="m-t-20"></SupportLabel>
    <!--    房间基本信息管理-->
    <SupportRoomBase class="m-t-20"></SupportRoomBase>
    <!--    杂费信息管理-->
    <SupportFee class="m-t-20"></SupportFee>
    <!--    租期管理-->
    <SupportTerm class="m-t-20"></SupportTerm>
    <!--    支付方式管理-->
    <SupportPayment class="m-t-20"></SupportPayment>
  </el-card>
</template>

<script setup lang="tsx">
import SupportFacility from '@/views/apartmentManagement/attributeManagement/components/supportFacility.vue'
import SupportLabel from '@/views/apartmentManagement/attributeManagement/components/supportLabel.vue'
import SupportRoomBase from '@/views/apartmentManagement/attributeManagement/components/supportRoomBase.vue'
import SupportFee from '@/views/apartmentManagement/attributeManagement/components/supportFee.vue'
import SupportTerm from '@/views/apartmentManagement/attributeManagement/components/supportTerm.vue'
import SupportPayment from '@/views/apartmentManagement/attributeManagement/components/supportPayment.vue'
</script>
