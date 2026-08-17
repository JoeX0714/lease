package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.NotificationType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.service.NotificationService;
import com.atguigu.lease.web.admin.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class LeaseExpireReminderTask {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Shanghai")
    public void sendLeaseExpireReminder() {
        //未来3天内即将到期
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date endDate = calendar.getTime();

        List<LeaseAgreement> expiringLeaseList = leaseAgreementService.selectExpiringLeaseList(startDate, endDate);
        for (LeaseAgreement leaseAgreement : expiringLeaseList) {
            //单条通知失败不影响其他用户提醒
            try {
                if (leaseAgreement.getPhone() == null) {
                    continue;
                }
                UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                        .eq(UserInfo::getPhone, leaseAgreement.getPhone()));
                if (userInfo == null) {
                    continue;
                }
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
                String expireDate = leaseAgreement.getLeaseEndDate() == null ? ""
                        : new SimpleDateFormat("yyyy-MM-dd").format(leaseAgreement.getLeaseEndDate());

                NotificationInfo notificationInfo = new NotificationInfo();
                notificationInfo.setUserId(userInfo.getId());
                notificationInfo.setType(NotificationType.LEASE_EXPIRE_REMINDER);
                notificationInfo.setBizType("AGREEMENT");
                notificationInfo.setBizId(leaseAgreement.getId());
                notificationInfo.setTitle("租约到期提醒");
                notificationInfo.setContent("您租住的【" + apartmentName + "】【" + roomNumber + "】房间租约将于 " + expireDate + " 到期，请及时处理续租或退租。");
                notificationService.createNotificationIfNotExistsToday(notificationInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
