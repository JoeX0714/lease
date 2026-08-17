package com.atguigu.lease.web.app.service.ai.memory;

import com.atguigu.lease.common.constant.RedisConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RedisAiConversationContextStore {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisAiConversationContextStore(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public RoomSelectionState load(String conversationId) {
        String json = stringRedisTemplate.opsForValue().get(buildKey(conversationId));
        if (json == null || json.isBlank()) {
            return new RoomSelectionState(new LinkedHashMap<>(), null);
        }
        try {
            RoomSelectionState state = objectMapper.readValue(json, RoomSelectionState.class);
            return new RoomSelectionState(
                    state.indexToRoomId() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(state.indexToRoomId()),
                    state.currentRoomId());
        } catch (JsonProcessingException e) {
            stringRedisTemplate.delete(buildKey(conversationId));
            return new RoomSelectionState(new LinkedHashMap<>(), null);
        }
    }

    public void save(String conversationId, RoomSelectionState state) {
        if (conversationId == null || conversationId.isBlank() || state == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(state);
            stringRedisTemplate.opsForValue().set(buildKey(conversationId), json,
                    Duration.ofSeconds(RedisConstant.AI_CONTEXT_TTL_SEC));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AI conversation context", e);
        }
    }

    private String buildKey(String conversationId) {
        return RedisConstant.AI_CONVERSATION_CONTEXT_PREFIX + conversationId;
    }

    public record RoomSelectionState(Map<Integer, Long> indexToRoomId, Long currentRoomId) {
    }
}
