package com.atguigu.lease.web.app.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "房间查询实体")
public class RoomQueryVo {

    @Schema(description = "省份Id")
    private Long provinceId;

    @Schema(description = "城市Id")
    private Long cityId;

    @Schema(description = "区域Id")
    private Long districtId;

    @Schema(description = "最小租金")
    private BigDecimal minRent;

    @Schema(description = "最大租金")
    private BigDecimal maxRent;

    @Schema(description = "支付方式")
    private Long paymentTypeId;

    @Schema(description = "价格排序方式", allowableValues = {"desc", "asc"})
    private String orderType;

    @Schema(description = "区域名称")
    private String districtName;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "户型或房间属性")
    private String attrName;

    @Schema(description = "设施名称列表")
    private List<String> facilityNames;

    @Schema(description = "标签名称列表")
    private List<String> labelNames;
}
