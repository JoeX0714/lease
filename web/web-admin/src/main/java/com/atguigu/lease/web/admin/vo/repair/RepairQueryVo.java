package com.atguigu.lease.web.admin.vo.repair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报修查询信息")
public class RepairQueryVo {

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户手机号")
    private String phone;

}
