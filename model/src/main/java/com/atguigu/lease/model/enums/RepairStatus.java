package com.atguigu.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RepairStatus implements BaseEnum {

    PENDING(1, "待受理"),

    COMPLETED(2, "已办结"),

    CANCELED(3, "已撤销");


    @EnumValue
    @JsonValue
    private Integer code;


    private String name;

    RepairStatus(Integer code, String name) {
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
