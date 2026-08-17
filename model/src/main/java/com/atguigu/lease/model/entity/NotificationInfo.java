package com.atguigu.lease.model.entity;

import com.atguigu.lease.model.enums.NotificationType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "通知信息表")
@TableName(value = "notification_info")
@Data
public class NotificationInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知用户id")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "通知类型")
    @TableField(value = "type")
    private NotificationType type;

    @Schema(description = "通知标题")
    @TableField(value = "title")
    private String title;

    @Schema(description = "通知内容")
    @TableField(value = "content")
    private String content;

    @Schema(description = "业务类型")
    @TableField(value = "biz_type")
    private String bizType;

    @Schema(description = "业务id")
    @TableField(value = "biz_id")
    private Long bizId;

    @Schema(description = "是否已读")
    @TableField(value = "is_read")
    private Boolean isRead;

    @Schema(description = "通知去重键")
    @TableField(value = "dedupe_key")
    @JsonIgnore
    private String dedupeKey;

}
