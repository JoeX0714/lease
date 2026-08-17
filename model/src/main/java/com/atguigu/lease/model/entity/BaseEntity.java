package com.atguigu.lease.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    @TableField(value = "create_time",fill= FieldFill.INSERT)//fill指定自动填充时机，而自动填充内容需要新建MybatisMetaObjectHandler类
    @JsonIgnore//加了该注解的字段不会出现在接口中
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time",fill= FieldFill.UPDATE)
    @JsonIgnore
    private Date updateTime;

    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    @TableLogic//逻辑删除，加了该注解的字段会自动进行逻辑删除，接口中不会返回，默认0为保留字段，1为删除
    @JsonIgnore
    private Byte isDeleted;

}