package com.atguigu.lease.web.app.mapper;

import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.model.enums.NotificationType;
import com.atguigu.lease.web.app.vo.notification.NotificationItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【notification_info(通知信息表)】的数据库操作Mapper
 * @Entity com.atguigu.lease.model.entity.NotificationInfo
 */
public interface NotificationMapper extends BaseMapper<NotificationInfo> {

    List<NotificationItemVo> listByUserId(Long userId);

    int countByUnique(@Param("userId") Long userId,
                      @Param("type") NotificationType type,
                      @Param("bizType") String bizType,
                      @Param("bizId") Long bizId);

    int countByUniqueToday(@Param("userId") Long userId,
                           @Param("type") NotificationType type,
                           @Param("bizType") String bizType,
                           @Param("bizId") Long bizId);
}
