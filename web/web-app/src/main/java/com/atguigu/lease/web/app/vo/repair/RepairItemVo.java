package com.atguigu.lease.web.app.vo.repair;

import com.atguigu.lease.model.entity.RepairInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报修列表信息")
public class RepairItemVo extends RepairInfo {

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "报修提交时间")
    private String createTimeStr;

}
