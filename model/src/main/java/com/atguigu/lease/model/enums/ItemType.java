package com.atguigu.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ItemType implements BaseEnum {

    APARTMENT(1, "公寓"),

    ROOM(2, "房间");

    /*当前端发起请求时，例如根据类型查询标签，传入code=1，Spring调用ConverterFactory，将1（这里的1是String类型，
    前端传入的所有数据都可看作String类型）转化为枚举对象，Controller接收到枚举对象，mybatis生成sql，进行数据库查询，Jackson序列化
    查询结构并返回json，@EnumValue告诉mybatis，这个枚举写入sql时使用code字段，@JsonValue 则告诉 Jackson：枚举转换成 JSON 时，
    只输出 code*/
    @EnumValue
    @JsonValue
    private Integer code;
    private String name;

    @Override
    public Integer getCode() {
        return this.code;
    }


    @Override
    public String getName() {
        return name;
    }

    ItemType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
