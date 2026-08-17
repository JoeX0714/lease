package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.web.app.dto.ai.AiChatRequest;
import com.atguigu.lease.web.app.vo.ai.AiChatResponse;
import com.atguigu.lease.web.app.vo.ai.AiHistoryMessageVo;

import java.util.List;

public interface AiChatService {

    AiChatResponse chat(AiChatRequest request);

    List<AiHistoryMessageVo> history(String sessionId);
}
