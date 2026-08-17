package com.atguigu.lease.web.admin.schedule;


import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component//把这个对象交给spring容器管理
public class ScheduleTasks {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron="0 0 0 * * ?")
    public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> leaseStatusUpdateWrapper = new LambdaUpdateWrapper<>();
        leaseStatusUpdateWrapper.le(LeaseAgreement::getLeaseEndDate,new Date());
        leaseStatusUpdateWrapper.in(LeaseAgreement::getStatus,LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING);//只有状态是已签约或退租待确认才需要检查租约是否过期
        leaseStatusUpdateWrapper.set(LeaseAgreement::getStatus,LeaseStatus.EXPIRED);
        leaseAgreementService.update(leaseStatusUpdateWrapper);
    }
}
