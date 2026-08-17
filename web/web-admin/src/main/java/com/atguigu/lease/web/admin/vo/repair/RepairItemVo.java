package com.atguigu.lease.web.admin.vo.repair;

import com.atguigu.lease.model.entity.RepairInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报修信息")
public class RepairItemVo extends RepairInfo {

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "报修用户姓名")
    private String userName;

    @Schema(description = "报修用户手机号")
    private String userPhone;

    @Schema(description = "报修提交时间")
    private String createTimeStr;

}
