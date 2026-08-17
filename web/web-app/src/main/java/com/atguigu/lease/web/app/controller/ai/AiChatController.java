package com.atguigu.lease.web.app.controller.ai;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.app.dto.ai.AiChatRequest;
import com.atguigu.lease.web.app.service.ai.AiChatService;
import com.atguigu.lease.web.app.vo.ai.AiChatResponse;
import com.atguigu.lease.web.app.vo.ai.AiHistoryMessageVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return Result.ok(aiChatService.chat(request));
    }

    @GetMapping("/history")
    public Result<List<AiHistoryMessageVo>> history(@RequestParam String sessionId) {
        return Result.ok(aiChatService.history(sessionId));
    }
}
