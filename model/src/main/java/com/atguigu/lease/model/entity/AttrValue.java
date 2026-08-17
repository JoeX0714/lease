package com.atguigu.lease.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Schema(description = "房间基本属性值表")
@TableName(value = "attr_value")
@Data
public class AttrValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "属性value")
    @TableField(value = "name")
    private String name;

    @Schema(description = "对应的属性key_id")
    @TableField(value = "attr_key_id")
    private Long attrKeyId;
}