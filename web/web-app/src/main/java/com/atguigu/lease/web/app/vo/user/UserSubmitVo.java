package com.atguigu.lease.web.app.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户修改信息")
public class UserSubmitVo {

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像图片URL")
    private String avatarUrl;
}
