package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.LeaseSourceType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.model.enums.LeaseStatusRules;
import com.atguigu.lease.model.enums.NotificationType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.service.NotificationService;
import com.atguigu.lease.web.admin.service.UserInfoService;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(LeaseAgreement entity) {
        boolean isUpdate = entity.getId() != null;
        LeaseStatus oldStatus = null;
        if (isUpdate) {
            LeaseAgreement old = getById(entity.getId());
            oldStatus = old == null ? null : old.getStatus();
        }
        validateLeaseWorkflow(entity, isUpdate);
        if (isUpdate && oldStatus != null && entity.getStatus() != null
                && !LeaseStatusRules.canTransition(oldStatus, entity.getStatus())) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "Illegal lease status transition");
        }
        boolean result = super.saveOrUpdate(entity);
        if (isUpdate && result && oldStatus != null
                && oldStatus != LeaseStatus.SIGNED && entity.getStatus() == LeaseStatus.SIGNED) {
            createLeaseSignedNotification(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long id, LeaseStatus status) {
        LeaseAgreement old = getById(id);
        if (old == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "租约不存在");
        }
        LeaseStatus oldStatus = old.getStatus();
        //幂等：已是目标状态，无需更新，不重复通知
        if (oldStatus == status) {
            return true;
        }

        //状态流转校验
        boolean valid = LeaseStatusRules.canTransition(oldStatus, status);
        if (!valid) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "非法的租约状态跳转");
        }

        //条件更新实现乐观并发：以读取到的旧状态作为 CAS 条件，未命中说明已被并发修改
        LambdaUpdateWrapper<LeaseAgreement> leaseAgreementUpdateWrapper = new LambdaUpdateWrapper<>();
        leaseAgreementUpdateWrapper.eq(LeaseAgreement::getId, id);
        leaseAgreementUpdateWrapper.eq(LeaseAgreement::getStatus, oldStatus);
        leaseAgreementUpdateWrapper.set(LeaseAgreement::getStatus, status);
        if (!update(leaseAgreementUpdateWrapper)) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "租约状态已被并发修改，请刷新后重试");
        }

        //签约成功通知（进入已签约）
        if (oldStatus != LeaseStatus.SIGNED && status == LeaseStatus.SIGNED) {
            createLeaseSignedNotification(old);
        }
        //退租审核结果通知（退租待确认 -> 已退租）
        if (status == LeaseStatus.WITHDRAWN) {
            createCheckOutResultNotification(old);
        }
        return true;
    }

    private void createLeaseSignedNotification(LeaseAgreement leaseAgreement) {
        String apartmentName = "";
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());
        if (apartmentInfo != null) {
            apartmentName = apartmentInfo.getName();
        }
        String roomNumber = "";
        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());
        if (roomInfo != null) {
            roomNumber = roomInfo.getRoomNumber();
        }
        String startDate = leaseAgreement.getLeaseStartDate() == null ? ""
                : new SimpleDateFormat("yyyy-MM-dd").format(leaseAgreement.getLeaseStartDate());
        String endDate = leaseAgreement.getLeaseEndDate() == null ? ""
                : new SimpleDateFormat("yyyy-MM-dd").format(leaseAgreement.getLeaseEndDate());

        UserInfo userInfo = leaseAgreement.getPhone() == null ? null
                : userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, leaseAgreement.getPhone()));
        if (userInfo == null) {
            return;
        }
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setUserId(userInfo.getId());
        notificationInfo.setType(NotificationType.LEASE_SIGNED);
        notificationInfo.setBizType("AGREEMENT");
        notificationInfo.setBizId(leaseAgreement.getId());
        notificationInfo.setTitle("签约成功");
        notificationInfo.setContent("您已成功签约【" + apartmentName + "】【" + roomNumber + "】房间，租期：" + startDate + " 至 " + endDate + "。");
        notificationService.createNotification(notificationInfo);
    }

    private void createCheckOutResultNotification(LeaseAgreement leaseAgreement) {
        UserInfo userInfo = leaseAgreement.getPhone() == null ? null
                : userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, leaseAgreement.getPhone()));
        if (userInfo == null) {
            return;
        }
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setUserId(userInfo.getId());
        notificationInfo.setType(NotificationType.CHECK_OUT_RESULT);
        notificationInfo.setBizType("AGREEMENT");
        notificationInfo.setBizId(leaseAgreement.getId());
        notificationInfo.setTitle("退租审核结果");
        notificationInfo.setContent("您的退租申请已审核完成，房间已退租。");
        notificationService.createNotification(notificationInfo);
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

    @Override
    public List<LeaseAgreement> selectExpiringLeaseList(Date startDate, Date endDate) {
        //到期提醒仅针对已签约(2)的当前占用租约；退租待确认(5)不再发送续租/到期提醒
        LambdaQueryWrapper<LeaseAgreement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseAgreement::getStatus, LeaseStatus.SIGNED);
        queryWrapper.ge(LeaseAgreement::getLeaseEndDate, startDate);
        queryWrapper.le(LeaseAgreement::getLeaseEndDate, endDate);
        return list(queryWrapper);
    }

    @Override
    public IPage<AgreementVo> pageAgreementByQuery(IPage<AgreementVo> page, AgreementQueryVo queryVo) {
        return leaseAgreementMapper.pageAgreementByQuery(page,queryVo);
    }

    @Override
    public AgreementVo getAgreementById(Long id) {
        //leaseAgreement
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);
        if (leaseAgreement == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }

        //签约公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(
                leaseAgreement.getApartmentId());

        //签约房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());

        //支付方式
        PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());

        //租期
        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());

        //组装
        AgreementVo agreementVo = new AgreementVo();
        BeanUtils.copyProperties(leaseAgreement,agreementVo);
        agreementVo.setApartmentInfo(apartmentInfo);
        agreementVo.setRoomInfo(roomInfo);
        agreementVo.setPaymentType(paymentType);
        agreementVo.setLeaseTerm(leaseTerm);
        return agreementVo;
    }
}




