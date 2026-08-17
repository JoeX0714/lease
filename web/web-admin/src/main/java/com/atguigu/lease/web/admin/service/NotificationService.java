package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.model.entity.NotificationInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author liubo
 * @description 针对表【notification_info(通知信息表)】的数据库操作Service
 * @createDate 2026-08-10
 */
public interface NotificationService extends IService<NotificationInfo> {

    void createNotification(NotificationInfo notificationInfo);

    boolean createNotificationIfNotExists(NotificationInfo notificationInfo);

    boolean createNotificationIfNotExistsToday(NotificationInfo notificationInfo);
}
