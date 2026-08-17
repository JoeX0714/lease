package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.web.app.mapper.NotificationMapper;
import com.atguigu.lease.web.app.service.NotificationService;
import com.atguigu.lease.web.app.vo.notification.NotificationItemVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【notification_info(通知信息表)】的数据库操作Service实现
 * @createDate 2026-08-10
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, NotificationInfo>
        implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void createNotification(NotificationInfo notificationInfo) {
        notificationInfo.setIsRead(false);
        notificationMapper.insert(notificationInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createNotificationIfNotExists(NotificationInfo notificationInfo) {
        //一次性业务事件：userId + type + bizType + bizId 整个生命周期只生成一次
        int count = notificationMapper.countByUnique(
                notificationInfo.getUserId(),
                notificationInfo.getType(),
                notificationInfo.getBizType(),
                notificationInfo.getBizId());
        if (count > 0) {
            return false;
        }
        return insertWithDedupeKey(notificationInfo, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createNotificationIfNotExistsToday(NotificationInfo notificationInfo) {
        //每日定时提醒：userId + type + bizType + bizId 当天只生成一次
        int count = notificationMapper.countByUniqueToday(
                notificationInfo.getUserId(),
                notificationInfo.getType(),
                notificationInfo.getBizType(),
                notificationInfo.getBizId());
        if (count > 0) {
            return false;
        }
        return insertWithDedupeKey(notificationInfo, true);
    }

    private boolean insertWithDedupeKey(NotificationInfo notificationInfo, boolean daily) {
        String dedupeKey = notificationInfo.getType().name()
                + ":" + notificationInfo.getBizType()
                + ":" + notificationInfo.getBizId();
        if (daily) {
            dedupeKey = dedupeKey + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        notificationInfo.setDedupeKey(dedupeKey);
        notificationInfo.setIsRead(false);
        try {
            notificationMapper.insert(notificationInfo);
            return true;
        } catch (DuplicateKeyException e) {
            //并发下唯一键兜底：视为该通知已存在
            return false;
        }
    }

    @Override
    public List<NotificationItemVo> listItemByUserId(Long userId) {
        return notificationMapper.listByUserId(userId);
    }

    @Override
    public void readById(Long userId, Long id) {
        LambdaUpdateWrapper<NotificationInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NotificationInfo::getId, id);
        updateWrapper.eq(NotificationInfo::getUserId, userId);
        updateWrapper.set(NotificationInfo::getIsRead, true);
        notificationMapper.update(null, updateWrapper);
    }
}
