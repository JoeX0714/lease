package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.model.enums.LeaseSourceType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.model.enums.LeaseStatusRules;
import com.atguigu.lease.model.enums.NotificationType;
import com.atguigu.lease.web.app.mapper.LeaseAgreementMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.service.NotificationService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private RoomInfoService roomInfoService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<AgreementItemVo> listItemByPhone(String phone) {
        return leaseAgreementMapper.listItemByPhone(phone);
    }

    @Override
    public AgreementDetailVo getDetailById(Long id) {
        return leaseAgreementMapper.getDetailById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LeaseAgreement applyCheckOut(Long agreementId) {
        if (agreementId == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR);
        }
        LeaseAgreement leaseAgreement = getById(agreementId);
        if (leaseAgreement == null || leaseAgreement.getIsDeleted() == 1) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }
        if (leaseAgreement.getPhone() == null
                || !leaseAgreement.getPhone().equals(LoginUserHolder.getLoginUser().getUsername())) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        if (leaseAgreement.getStatus() == LeaseStatus.WITHDRAWING) {
            return leaseAgreement;
        }
        if (leaseAgreement.getStatus() != LeaseStatus.SIGNED) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "Only signed lease agreements can apply for check-out");
        }
        leaseAgreement.setStatus(LeaseStatus.WITHDRAWING);
        saveOrUpdate(leaseAgreement);
        return leaseAgreement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(LeaseAgreement entity) {
        LeaseStatus newStatus = entity.getStatus();
        boolean isUpdate = entity.getId() != null;
        LeaseStatus oldStatus = null;
        if (isUpdate) {
            LeaseAgreement old = getById(entity.getId());
            oldStatus = old == null ? null : old.getStatus();
        }

        //状态流转校验（仅更新场景）
        validateLeaseWorkflow(entity, isUpdate);

        if (isUpdate && oldStatus != null && newStatus != null) {
            if (!LeaseStatusRules.canTransition(oldStatus, newStatus)) {
                throw new LeaseException(ResultCodeEnum.DATA_ERROR);
            }
        }

        boolean result = super.saveOrUpdate(entity);

        //签约成功通知（待签约 -> 已签约）
        if (isUpdate && result && oldStatus != null
                && oldStatus != LeaseStatus.SIGNED && newStatus == LeaseStatus.SIGNED) {
            createLeaseSignedNotification(entity);
        }
        return result;
    }

    private void createLeaseSignedNotification(LeaseAgreement entity) {
        String apartmentName = "";
        ApartmentInfo apartmentInfo = apartmentInfoService.getById(entity.getApartmentId());
        if (apartmentInfo != null) {
            apartmentName = apartmentInfo.getName();
        }
        String roomNumber = "";
        RoomInfo roomInfo = roomInfoService.getById(entity.getRoomId());
        if (roomInfo != null) {
            roomNumber = roomInfo.getRoomNumber();
        }
        String startDate = entity.getLeaseStartDate() == null ? ""
                : new SimpleDateFormat("yyyy-MM-dd").format(entity.getLeaseStartDate());
        String endDate = entity.getLeaseEndDate() == null ? ""
                : new SimpleDateFormat("yyyy-MM-dd").format(entity.getLeaseEndDate());

        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setUserId(LoginUserHolder.getLoginUser().getUserId());
        notificationInfo.setType(NotificationType.LEASE_SIGNED);
        notificationInfo.setBizType("AGREEMENT");
        notificationInfo.setBizId(entity.getId());
        notificationInfo.setTitle("签约成功");
        notificationInfo.setContent("您已成功签约【" + apartmentName + "】【" + roomNumber + "】房间，租期：" + startDate + " 至 " + endDate + "。");
        notificationService.createNotificationIfNotExists(notificationInfo);
    }
    private void validateLeaseWorkflow(LeaseAgreement entity, boolean isUpdate) {
        if (entity.getRoomId() == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR);
        }
        if (isUpdate) {
            return;
        }
        if (isRenewAgreement(entity)) {
            validateRenewAgreement(entity);
            return;
        }
        LambdaQueryWrapper<LeaseAgreement> agreementWrapper = new LambdaQueryWrapper<>();
        agreementWrapper.eq(LeaseAgreement::getRoomId, entity.getRoomId());
        agreementWrapper.in(LeaseAgreement::getStatus, LeaseStatusRules.newWorkflowBlockingStatuses());
        if (count(agreementWrapper) > 0) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "Room already has an active lease workflow");
        }
    }

    private boolean isRenewAgreement(LeaseAgreement entity) {
        return entity.getSourceType() == LeaseSourceType.RENEW || entity.getStatus() == LeaseStatus.RENEWING;
    }

    private void validateRenewAgreement(LeaseAgreement entity) {
        if (entity.getStatus() != LeaseStatus.RENEWING || entity.getPhone() == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR);
        }
        LambdaQueryWrapper<LeaseAgreement> signedWrapper = new LambdaQueryWrapper<>();
        signedWrapper.eq(LeaseAgreement::getRoomId, entity.getRoomId());
        signedWrapper.eq(LeaseAgreement::getPhone, entity.getPhone());
        signedWrapper.eq(LeaseAgreement::getStatus, LeaseStatus.SIGNED);
        if (count(signedWrapper) == 0) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "Renewal requires an existing signed lease");
        }
        LambdaQueryWrapper<LeaseAgreement> renewingWrapper = new LambdaQueryWrapper<>();
        renewingWrapper.eq(LeaseAgreement::getRoomId, entity.getRoomId());
        renewingWrapper.eq(LeaseAgreement::getPhone, entity.getPhone());
        renewingWrapper.eq(LeaseAgreement::getStatus, LeaseStatus.RENEWING);
        if (count(renewingWrapper) > 0) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "Renewal workflow already exists");
        }
    }
}
