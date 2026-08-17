package com.atguigu.lease.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Schema(description = "城市信息表")
@TableName(value = "city_info")
@Data
public class CityInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "城市名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "所属省份id")
    @TableField(value = "province_id")
    private Integer provinceId;

}