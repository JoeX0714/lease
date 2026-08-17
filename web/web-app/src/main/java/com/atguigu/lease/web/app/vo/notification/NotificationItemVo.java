package com.atguigu.lease.web.app.vo.notification;

import com.atguigu.lease.model.entity.NotificationInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通知列表信息")
public class NotificationItemVo extends NotificationInfo {

    @Schema(description = "通知创建时间")
    private String createTimeStr;

}
