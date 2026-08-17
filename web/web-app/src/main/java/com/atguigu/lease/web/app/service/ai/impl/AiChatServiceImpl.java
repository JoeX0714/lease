package com.atguigu.lease.web.app.service.ai.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.web.app.dto.ai.AiChatRequest;
import com.atguigu.lease.web.app.service.ai.AiConversationContext;
import com.atguigu.lease.web.app.service.ai.AiChatService;
import com.atguigu.lease.web.app.service.ai.AppointmentTools;
import com.atguigu.lease.web.app.service.ai.LeaseTools;
import com.atguigu.lease.web.app.service.ai.NotificationTools;
import com.atguigu.lease.web.app.service.ai.RepairTools;
import com.atguigu.lease.web.app.service.ai.RoomTools;
import com.atguigu.lease.web.app.service.ai.memory.RedisChatMemoryRepository;
import com.atguigu.lease.web.app.vo.ai.AiChatResponse;
import com.atguigu.lease.web.app.vo.ai.AiHistoryMessageVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final String SYSTEM_PROMPT = """
            你是“尚庭公寓 AI 租房助手”。
            你只能根据系统提供的真实 Tool 查询和操作业务数据。
            涉及房源列表、房源价格、房源地址、房源设施、房源详情、用户预约、创建预约时，必须调用 Tool 获取真实数据。
            禁止编造任何业务数据，禁止假设数据库中存在某套房。
            searchRooms 返回的每条房源都有 index 和 roomId，但面向用户回答时不需要展示 roomId。
            用户说“第二套”“这套”“刚才那个”“帮我预约它”时，优先调用 resolveRoomReference 根据当前会话中的 index 或当前讨论房源解析真实 roomId，再调用 getRoomDetail 或 createViewingAppointment。
            用户要求预约时必须确定具体房源、日期和时间；信息不足先追问；信息完整后调用 createViewingAppointment；只有 Tool 返回 success=true 后才能说明预约成功。
            日期按中国时区理解。用户说“明天”时，请根据当前日期推断具体 yyyy-MM-dd；时间不明确如“下午”时，先追问具体时间。
            用户说“附近”时，当前系统只能按关键词、公寓名、地址等文本信息匹配，不能声称做了经纬度距离计算、距离 X 公里或按距离排序。
            如果严格户型条件没有结果，不要静默放宽条件；必须说明没有严格匹配，再询问是否查看相近房型。
            回答要简洁、自然，优先列出真实房源的序号、房号、公寓名、区域、地址和租金。
            """;

    private static final String SECOND_STAGE_PROMPT = """

            第二阶段新增规则：
            你还可以通过 Tool 查询我的房间、租约状态、我的报修、业务通知，并创建报修或申请退租。
            涉及我的房间、租约、报修、通知、退租时，必须调用 Tool 获取真实业务数据，禁止编造。
            Tool 不接受 userId；用户身份只能来自后端登录上下文。
            如果 Tool 返回 requiresSelection=true，说明当前用户有多个有效租约且无法唯一确定房间，你必须先让用户选择具体房间，不能默认选择第一条。
            创建报修、创建看房预约、申请退租都属于写操作，必须有明确执行意图；用户只是询问流程时只能解释，不能调用写操作 Tool。
            applyCheckOut 只能发起退租申请，结果是租约进入“退租待确认”，后续必须等待后台审核；禁止说已经完成退租。
            租约状态为 WITHDRAWING/退租待确认 时，房间仍被当前用户占用，仍属于当前有效租约；用户问是否已经退租时，必须说明“已提交退租申请，正在等待后台确认，尚未正式退租”。
            getMessages 只查询通知，不能标记已读。
            """;

    private final ChatClient chatClient;
    private final AiConversationContext conversationContext;
    private final RedisChatMemoryRepository redisChatMemoryRepository;

    @Autowired
    public AiChatServiceImpl(ChatClient.Builder chatClientBuilder, RoomTools roomTools,
                             AppointmentTools appointmentTools, LeaseTools leaseTools, RepairTools repairTools,
                             NotificationTools notificationTools, AiConversationContext conversationContext,
                             RedisChatMemoryRepository redisChatMemoryRepository) {
        this.conversationContext = conversationContext;
        this.redisChatMemoryRepository = redisChatMemoryRepository;
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(20)
                .build();
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT + SECOND_STAGE_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(roomTools, appointmentTools, leaseTools, repairTools, notificationTools)
                .build();
    }

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        String conversationId = loginUser.getUserId() + ":" + request.getSessionId().trim();
        String userMessage = "当前中国日期：" + LocalDate.now(ZoneId.of("Asia/Shanghai"))
                + "\n用户消息：" + request.getMessage();
        conversationContext.setCurrentConversationId(conversationId);
        try {
            String content = chatClient.prompt()
                    .user(userMessage)
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .call()
                    .content();
            return new AiChatResponse(content);
        } finally {
            conversationContext.clearCurrentConversationId();
        }
    }

    @Override
    public List<AiHistoryMessageVo> history(String sessionId) {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR);
        }
        String conversationId = loginUser.getUserId() + ":" + sessionId.trim();
        return redisChatMemoryRepository.findHistoryByConversationId(conversationId).stream()
                .map(this::toHistoryMessage)
                .filter(message -> message.getContent() != null && !message.getContent().trim().isEmpty())
                .toList();
    }

    private AiHistoryMessageVo toHistoryMessage(RedisChatMemoryRepository.SimpleHistoryMessage message) {
        String role = message.role();
        String content = message.content();
        if ("USER".equals(role)) {
            content = normalizeUserHistoryContent(content);
        }
        return new AiHistoryMessageVo(role, content);
    }

    private String normalizeUserHistoryContent(String content) {
        if (content == null) {
            return null;
        }
        int lineBreak = content.lastIndexOf('\n');
        String lastLine = lineBreak >= 0 ? content.substring(lineBreak + 1) : content;
        int colon = Math.max(lastLine.lastIndexOf('：'), lastLine.lastIndexOf(':'));
        if (colon >= 0 && colon + 1 < lastLine.length()) {
            return lastLine.substring(colon + 1).trim();
        }
        return lastLine.trim();
    }
}
