package com.atguigu.lease.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Schema(description = "杂项费用值表")
@TableName(value = "fee_value")
@Data
public class FeeValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "费用value")
    @TableField(value = "name")
    private String name;

    @Schema(description = "收费单位")
    @TableField(value = "unit")
    private String unit;

    @Schema(description = "费用所对的fee_key编码")
    @TableField(value = "fee_key_id")
    private Long feeKeyId;

}