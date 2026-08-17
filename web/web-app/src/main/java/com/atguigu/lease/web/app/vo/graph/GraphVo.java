package com.atguigu.lease.web.app.vo.graph;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Schema(description = "图片信息")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GraphVo implements Serializable {


    @Schema(description = "图片名称")
    private String name;

    @Schema(description = "图片地址")
    private String url;

}
