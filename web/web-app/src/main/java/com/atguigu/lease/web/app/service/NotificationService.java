package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.web.app.vo.notification.NotificationItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【notification_info(通知信息表)】的数据库操作Service
 * @createDate 2026-08-10
 */
public interface NotificationService extends IService<NotificationInfo> {

    void createNotification(NotificationInfo notificationInfo);

    boolean createNotificationIfNotExists(NotificationInfo notificationInfo);

    boolean createNotificationIfNotExistsToday(NotificationInfo notificationInfo);

    List<NotificationItemVo> listItemByUserId(Long userId);

    void readById(Long userId, Long id);
}
