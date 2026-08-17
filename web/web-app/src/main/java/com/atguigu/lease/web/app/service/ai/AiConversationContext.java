package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.web.app.service.ai.memory.RedisAiConversationContextStore;
import com.atguigu.lease.web.app.service.ai.memory.RedisAiConversationContextStore.RoomSelectionState;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AiConversationContext {

    private static final ThreadLocal<String> CURRENT_CONVERSATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<RoomSelectionState> CURRENT_STATE = new ThreadLocal<>();

    private final RedisAiConversationContextStore contextStore;

    public AiConversationContext(RedisAiConversationContextStore contextStore) {
        this.contextStore = contextStore;
    }

    public void setCurrentConversationId(String conversationId) {
        CURRENT_CONVERSATION_ID.set(conversationId);
        CURRENT_STATE.set(contextStore.load(conversationId));
    }

    public void clearCurrentConversationId() {
        CURRENT_CONVERSATION_ID.remove();
        CURRENT_STATE.remove();
    }

    public void saveSearchResults(Map<Integer, Long> indexToRoomId) {
        String conversationId = CURRENT_CONVERSATION_ID.get();
        if (conversationId == null) {
            return;
        }
        RoomSelectionState state = new RoomSelectionState(new LinkedHashMap<>(indexToRoomId), null);
        CURRENT_STATE.set(state);
        contextStore.save(conversationId, state);
    }

    public void saveCurrentRoomId(Long roomId) {
        String conversationId = CURRENT_CONVERSATION_ID.get();
        if (conversationId == null || roomId == null) {
            return;
        }
        RoomSelectionState current = currentState();
        RoomSelectionState state = new RoomSelectionState(new LinkedHashMap<>(current.indexToRoomId()), roomId);
        CURRENT_STATE.set(state);
        contextStore.save(conversationId, state);
    }

    public Long resolveRoomId(Integer index, Boolean current) {
        String conversationId = CURRENT_CONVERSATION_ID.get();
        if (conversationId == null) {
            return null;
        }
        RoomSelectionState state = currentState();
        if (Boolean.TRUE.equals(current)) {
            return state.currentRoomId();
        }
        if (index != null) {
            return state.indexToRoomId().get(index);
        }
        return state.currentRoomId();
    }

    public Map<Integer, Long> getCurrentIndexToRoomId() {
        String conversationId = CURRENT_CONVERSATION_ID.get();
        if (conversationId == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(currentState().indexToRoomId());
    }

    public Long getCurrentRoomId() {
        String conversationId = CURRENT_CONVERSATION_ID.get();
        if (conversationId == null) {
            return null;
        }
        return currentState().currentRoomId();
    }

    private RoomSelectionState currentState() {
        RoomSelectionState state = CURRENT_STATE.get();
        if (state != null) {
            return state;
        }
        String conversationId = CURRENT_CONVERSATION_ID.get();
        state = conversationId == null
                ? new RoomSelectionState(new LinkedHashMap<>(), null)
                : contextStore.load(conversationId);
        CURRENT_STATE.set(state);
        return state;
    }
}
