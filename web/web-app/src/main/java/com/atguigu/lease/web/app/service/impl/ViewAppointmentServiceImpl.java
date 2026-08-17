package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.model.enums.AppointmentStatus;
import com.atguigu.lease.model.enums.LeaseStatusRules;
import com.atguigu.lease.model.enums.NotificationType;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.app.mapper.LeaseAgreementMapper;
import com.atguigu.lease.web.app.mapper.RoomInfoMapper;
import com.atguigu.lease.web.app.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.NotificationService;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public boolean saveOrUpdate(ViewAppointment entity) {
        //分布式锁：同一房间 + 预约时间互斥（watchdog 自动续期，leaseTime=-1）
        String lockKey = "app:appointment:lock:" + entity.getRoomId() + ":"
                + (entity.getAppointmentTime() == null ? ""
                : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getAppointmentTime()));
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            try {
                locked = lock.tryLock(3, -1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new LeaseException(ResultCodeEnum.SERVICE_ERROR.getCode(), "系统繁忙，请稍后重试");
            }
            if (!locked) {
                throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "当前时段预约人数较多，请稍后重试");
            }
            //锁内事务：冲突检查 + 保存预约 + 创建通知，提交后 finally 释放锁
            return transactionTemplate.execute(status -> doSaveOrUpdate(entity));
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private boolean doSaveOrUpdate(ViewAppointment entity) {
        boolean isNew = entity.getId() == null;
        //新增预约时后端强制设置状态为待看房，禁止插入 NULL
        if (isNew) {
            entity.setAppointmentStatus(AppointmentStatus.WAITING);
        }
        //房间与时间合法性校验
        if (entity.getRoomId() == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "请选择预约房间");
        }
        if (isNew) {
            validateRoomCanEnterNewWorkflow(entity.getRoomId());
        }
        validateAppointmentTime(entity.getAppointmentTime());
        //更新场景校验：仅本人且仅待看房(WAITING)状态的预约允许修改
        if (!isNew) {
            ViewAppointment old = getById(entity.getId());
            if (old == null) {
                throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "预约不存在");
            }
            if (old.getUserId() == null || !old.getUserId().equals(LoginUserHolder.getLoginUser().getUserId())) {
                throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "预约不存在");
            }
            if (old.getAppointmentStatus() != AppointmentStatus.WAITING) {
                throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "当前预约状态不允许修改");
            }
        }
        //并发冲突校验：同一房间同一预约时间存在有效预约（待看房/已看房）时拒绝
        LambdaQueryWrapper<ViewAppointment> conflictWrapper = new LambdaQueryWrapper<>();
        conflictWrapper.eq(ViewAppointment::getRoomId, entity.getRoomId());
        conflictWrapper.eq(ViewAppointment::getAppointmentTime, entity.getAppointmentTime());
        conflictWrapper.in(ViewAppointment::getAppointmentStatus, AppointmentStatus.WAITING, AppointmentStatus.VIEWED);
        if (!isNew) {
            conflictWrapper.ne(ViewAppointment::getId, entity.getId());
        }
        if (count(conflictWrapper) > 0) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "该房间该时段已有预约");
        }
        boolean result = super.saveOrUpdate(entity);
        //预约创建成功后生成通知
        if (isNew && result) {
            NotificationInfo notificationInfo = new NotificationInfo();
            notificationInfo.setUserId(entity.getUserId());
            notificationInfo.setType(NotificationType.APPOINTMENT_SUCCESS);
            notificationInfo.setBizType("APPOINTMENT");
            notificationInfo.setBizId(entity.getId());
            notificationInfo.setTitle("预约成功");
            String apartmentName = "";
            ApartmentInfo apartmentInfo = apartmentInfoService.getById(entity.getApartmentId());
            if (apartmentInfo != null) {
                apartmentName = apartmentInfo.getName();
            }
            String appointmentTime = entity.getAppointmentTime() == null ? ""
                    : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getAppointmentTime());
            notificationInfo.setContent("您预约了【" + apartmentName + "】看房，预约时间：" + appointmentTime + "，请按时到店。");
            notificationService.createNotificationIfNotExists(notificationInfo);
        }
        return result;
    }

    private void validateAppointmentTime(Date appointmentTime) {
        if (appointmentTime == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "请选择预约时间");
        }
        LocalDateTime submittedDateTime = appointmentTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDate appointmentDate = submittedDateTime.toLocalDate();
        LocalTime appointmentLocalTime = submittedDateTime.toLocalTime();
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentLocalTime);
        if (!appointmentDateTime.isAfter(LocalDateTime.now())) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "预约时间必须晚于当前时间");
        }
        int hour = appointmentLocalTime.getHour();
        int minute = appointmentLocalTime.getMinute();
        int second = appointmentLocalTime.getSecond();
        if (second != 0) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "预约时间需为整点或半点");
        }
        if (minute != 0 && minute != 30) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "预约时间需为整点或半点");
        }
        if (hour < 7 || hour > 20) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "预约时间需在 07:00-20:30 之间");
        }
    }

    private void validateRoomCanEnterNewWorkflow(Long roomId) {
        RoomInfo roomInfo = roomInfoMapper.selectById(roomId);
        if (roomInfo == null || roomInfo.getIsDeleted() == 1 || roomInfo.getIsRelease() != ReleaseStatus.RELEASED) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "Room is not available for appointment");
        }
        LambdaQueryWrapper<LeaseAgreement> agreementWrapper = new LambdaQueryWrapper<>();
        agreementWrapper.eq(LeaseAgreement::getRoomId, roomId);
        agreementWrapper.in(LeaseAgreement::getStatus, LeaseStatusRules.newWorkflowBlockingStatuses());
        if (leaseAgreementMapper.selectCount(agreementWrapper) > 0) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "Room already has an active lease workflow");
        }
    }

    @Override
    public List<AppointmentItemVo> listItemByUserId(Long userId) {
        return viewAppointmentMapper.listItemByUserId(userId);
    }

    @Override
    public AppointmentDetailVo getDetailById(Long id) {
        ViewAppointment viewAppointment = viewAppointmentMapper.selectById(id);
        if (viewAppointment == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }
        ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(
                viewAppointment.getApartmentId());

        AppointmentDetailVo appointmentDetailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(viewAppointment, appointmentDetailVo);
        appointmentDetailVo.setApartmentItemVo(apartmentItemVo);

        return appointmentDetailVo;
    }
}
