package com.atguigu.lease.web.app.service.ai.memory;

import com.atguigu.lease.common.constant.RedisConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemoryRepository(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> findConversationIds() {
        return Collections.emptyList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String json = stringRedisTemplate.opsForValue().get(buildKey(conversationId));
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            StoredMessageList stored = objectMapper.readValue(json, StoredMessageList.class);
            if (stored.messages() == null) {
                return Collections.emptyList();
            }
            List<Message> messages = new ArrayList<>();
            for (StoredMessage message : stored.messages()) {
                Message restored = toMessage(message);
                if (restored != null) {
                    messages.add(restored);
                }
            }
            return messages;
        } catch (JsonProcessingException e) {
            deleteByConversationId(conversationId);
            return Collections.emptyList();
        }
    }

    public List<SimpleHistoryMessage> findHistoryByConversationId(String conversationId) {
        String json = stringRedisTemplate.opsForValue().get(buildKey(conversationId));
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            StoredMessageList stored = objectMapper.readValue(json, StoredMessageList.class);
            if (stored.messages() == null) {
                return Collections.emptyList();
            }
            return stored.messages().stream()
                    .filter(message -> message != null
                            && ("USER".equals(message.role()) || "ASSISTANT".equals(message.role()))
                            && message.content() != null
                            && !message.content().isBlank())
                    .map(message -> new SimpleHistoryMessage(message.role(), message.content()))
                    .toList();
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        if (conversationId == null || conversationId.isBlank()) {
            return;
        }
        List<StoredMessage> storedMessages = messages == null ? Collections.emptyList()
                : messages.stream()
                .map(this::toStoredMessage)
                .filter(message -> message != null && message.content() != null && !message.content().isBlank())
                .toList();
        try {
            String json = objectMapper.writeValueAsString(new StoredMessageList(storedMessages));
            stringRedisTemplate.opsForValue().set(buildKey(conversationId), json,
                    Duration.ofSeconds(RedisConstant.AI_CONTEXT_TTL_SEC));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AI chat memory", e);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        if (conversationId != null && !conversationId.isBlank()) {
            stringRedisTemplate.delete(buildKey(conversationId));
        }
    }

    private String buildKey(String conversationId) {
        return RedisConstant.AI_CHAT_MEMORY_PREFIX + conversationId;
    }

    private StoredMessage toStoredMessage(Message message) {
        if (message == null || message.getMessageType() == null) {
            return null;
        }
        String content = message.getText();
        if (message instanceof ToolResponseMessage toolResponseMessage) {
            content = toolResponseMessage.getResponses() == null ? null : toolResponseMessage.getResponses().toString();
        }
        return new StoredMessage(message.getMessageType().name(), content);
    }

    private Message toMessage(StoredMessage storedMessage) {
        if (storedMessage == null || storedMessage.role() == null) {
            return null;
        }
        String content = storedMessage.content() == null ? "" : storedMessage.content();
        MessageType messageType = MessageType.valueOf(storedMessage.role());
        return switch (messageType) {
            case USER -> new UserMessage(content);
            case ASSISTANT -> new AssistantMessage(content);
            case SYSTEM -> new SystemMessage(content);
            case TOOL -> null;
        };
    }

    private record StoredMessageList(List<StoredMessage> messages) {
    }

    private record StoredMessage(String role, String content) {
    }

    public record SimpleHistoryMessage(String role, String content) {
    }
}
