package com.atguigu.lease.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.atguigu.lease.model.enums.LeaseSourceType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "租约信息表")
@TableName(value = "lease_agreement")
@Data
public class LeaseAgreement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "承租人手机号码")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "承租人姓名")
    @TableField(value = "name")
    private String name;

    @Schema(description = "承租人身份证号码")
    @TableField(value = "identification_number")
    private String identificationNumber;

    @Schema(description = "签约公寓id")
    @TableField(value = "apartment_id")
    private Long apartmentId;

    @Schema(description = "签约房间id")
    @TableField(value = "room_id")
    private Long roomId;

    @Schema(description = "租约开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "lease_start_date")
    private Date leaseStartDate;

    @Schema(description = "租约结束日期")
    @TableField(value = "lease_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaseEndDate;

    @Schema(description = "租期id")
    @TableField(value = "lease_term_id")
    private Long leaseTermId;

    @Schema(description = "租金（元/月）")
    @TableField(value = "rent")
    private BigDecimal rent;

    @Schema(description = "押金（元）")
    @TableField(value = "deposit")
    private BigDecimal deposit;

    @Schema(description = "支付类型id")
    @TableField(value = "payment_type_id")
    private Long paymentTypeId;

    @Schema(description = "租约状态")
    @TableField(value = "status")
    private LeaseStatus status;

    @Schema(description = "租约来源")
    @TableField(value = "source_type")
    private LeaseSourceType sourceType;

    @Schema(description = "备注信息")
    @TableField(value = "additional_info")
    private String additionalInfo;

}