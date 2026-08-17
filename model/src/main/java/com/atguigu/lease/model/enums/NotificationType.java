package com.atguigu.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType implements BaseEnum {

    APPOINTMENT_SUCCESS(1, "预约成功"),

    REPAIR_UPDATE(2, "报修更新"),

    CHECK_OUT_RESULT(3, "退租审核结果"),

    LEASE_SIGNED(4, "签约成功"),

    LEASE_EXPIRE_REMINDER(5, "租约到期提醒");


    @EnumValue
    @JsonValue
    private Integer code;


    private String name;

    NotificationType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
