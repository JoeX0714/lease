package com.atguigu.lease.web.app.vo.repair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报修提交信息")
public class RepairSubmitVo {

    @Schema(description = "关联租约id")
    private Long agreementId;

    @Schema(description = "报修房间id")
    private Long roomId;

    @Schema(description = "报修内容")
    private String repairContent;

}
