package com.atguigu.lease.model.entity;

import com.atguigu.lease.model.enums.RepairStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Schema(description = "报修信息表")
@TableName(value = "repair_info")
@Data
public class RepairInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报修用户id")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "关联租约id")
    @TableField(value = "agreement_id")
    private Long agreementId;

    @Schema(description = "公寓id")
    @TableField(value = "apartment_id")
    private Long apartmentId;

    @Schema(description = "房间id")
    @TableField(value = "room_id")
    private Long roomId;

    @Schema(description = "报修内容")
    @TableField(value = "repair_content")
    private String repairContent;

    @Schema(description = "报修状态")
    @TableField(value = "status")
    private RepairStatus status;

}
