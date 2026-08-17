package com.atguigu.lease.web.app.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatRequest {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String message;
}
