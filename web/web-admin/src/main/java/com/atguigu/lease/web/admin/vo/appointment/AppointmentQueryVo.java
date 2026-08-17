package com.atguigu.lease.web.admin.vo.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预约看房查询实体")
public class AppointmentQueryVo {


    @Schema(description="预约公寓所在省份")
    private Long provinceId;

    @Schema(description="预约公寓所在城市")
    private Long cityId;

    @Schema(description="预约公寓所在区")
    private Long districtId;

    @Schema(description="预约公寓所在公寓")
    private Long apartmentId;

    @Schema(description="预约用户姓名")
    private String name;

    @Schema(description="预约用户手机号码")
    private String phone;

}
