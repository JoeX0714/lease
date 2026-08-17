package com.atguigu.lease.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "支付方式表")
@TableName(value = "payment_type")
@Data
public class PaymentType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "付款方式名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "每次支付租期数")
    @TableField(value = "pay_month_count")
    private String payMonthCount;

    @Schema(description = "付费说明")
    @TableField(value = "additional_info")
    private String additionalInfo;


}