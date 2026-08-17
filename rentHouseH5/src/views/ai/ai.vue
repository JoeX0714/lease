<template>
  <div class="ai-page">
    <div class="ai-header">AI 租房助手</div>

    <div ref="messageListRef" class="message-list">
      <div
        v-for="item in messages"
        :key="item.id"
        class="message-row"
        :class="item.role"
      >
        <div class="message-bubble">{{ item.content }}</div>
      </div>
      <div v-if="sending" class="message-row assistant">
        <div class="message-bubble loading-bubble">
          <van-loading size="16" />
          <span>正在思考...</span>
        </div>
      </div>
    </div>

    <div class="input-bar">
      <van-field
        v-model="inputValue"
        class="chat-input"
        type="textarea"
        autosize
        rows="1"
        maxlength="300"
        placeholder="说说你的租房需求"
        @keyup.enter.prevent="sendMessage"
      />
      <van-button
        class="send-button"
        type="primary"
        size="small"
        :loading="sending"
        :disabled="sending || !inputValue.trim()"
        @click="sendMessage"
      >
        发送
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="AiAssistant">
import { nextTick, onMounted, ref } from "vue";
import { showFailToast } from "vant";
import { chatWithAi, getAiHistory } from "@/api/ai";
import type { AiHistoryMessage } from "@/api/ai/types";

interface ChatMessage {
  id: number;
  role: "user" | "assistant";
  content: string;
}

const SESSION_KEY = "aiSessionId";
const WELCOME_MESSAGE =
  "你好，我是尚庭公寓 AI 租房助手。可以告诉我区域、预算、户型或设施要求，我会帮你找真实房源。";

const inputValue = ref("");
const historyLoading = ref(false);
const sending = ref(false);
const hasUserInteraction = ref(false);
const messageListRef = ref<HTMLElement>();
const messages = ref<ChatMessage[]>([buildWelcomeMessage()]);

function getSessionId() {
  let sessionId = localStorage.getItem(SESSION_KEY);
  if (!sessionId) {
    sessionId = createSessionId();
    localStorage.setItem(SESSION_KEY, sessionId);
  }
  return sessionId;
}

function createSessionId() {
  if (window.crypto && "randomUUID" in window.crypto) {
    return window.crypto.randomUUID();
  }
  return `${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

function buildWelcomeMessage(): ChatMessage {
  return {
    id: Date.now(),
    role: "assistant",
    content: WELCOME_MESSAGE
  };
}

function toChatMessage(item: AiHistoryMessage, index: number): ChatMessage {
  return {
    id: Date.now() + index,
    role: item.role === "USER" ? "user" : "assistant",
    content: item.content
  };
}

async function restoreHistory() {
  historyLoading.value = true;
  try {
    const { data } = await getAiHistory(getSessionId());
    if (!hasUserInteraction.value) {
      messages.value = data && data.length > 0 ? data.map(toChatMessage) : [buildWelcomeMessage()];
    }
  } catch (error) {
    console.log(error);
    if (!hasUserInteraction.value) {
      messages.value = [buildWelcomeMessage()];
    }
  } finally {
    historyLoading.value = false;
    await scrollToBottom();
  }
}

async function scrollToBottom() {
  await nextTick();
  const el = messageListRef.value;
  if (el) {
    el.scrollTop = el.scrollHeight;
  }
}

async function sendMessage() {
  const content = inputValue.value.trim();
  if (!content || sending.value) {
    return;
  }
  hasUserInteraction.value = true;
  messages.value.push({
    id: Date.now(),
    role: "user",
    content
  });
  inputValue.value = "";
  sending.value = true;
  await scrollToBottom();
  try {
    const { data } = await chatWithAi({
      sessionId: getSessionId(),
      message: content
    });
    messages.value.push({
      id: Date.now() + 1,
      role: "assistant",
      content: data.content || "我暂时没有得到有效回复，请再试一次。"
    });
  } catch (error) {
    console.log(error);
    showFailToast("AI 助手暂时不可用，请稍后再试");
    messages.value.push({
      id: Date.now() + 1,
      role: "assistant",
      content: "抱歉，当前没有成功连接到 AI 助手。"
    });
  } finally {
    sending.value = false;
    await scrollToBottom();
  }
}

onMounted(restoreHistory);
</script>

<style lang="less" scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f6f7f9;
}

.ai-header {
  flex: 0 0 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #202124;
  background: #fff;
  border-bottom: 1px solid #eef0f3;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 14px 12px 96px;
}

.message-row {
  display: flex;
  margin-bottom: 12px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.assistant {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 78%;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-row.user .message-bubble {
  color: #fff;
  background: #1989fa;
}

.message-row.assistant .message-bubble {
  color: #202124;
  background: #fff;
  box-shadow: 0 2px 8px rgba(27, 31, 35, 0.06);
}

.loading-bubble {
  display: flex;
  gap: 8px;
  align-items: center;
}

.input-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 50px;
  display: flex;
  gap: 8px;
  align-items: flex-end;
  padding: 8px 10px;
  background: #fff;
  border-top: 1px solid #eef0f3;
}

.chat-input {
  flex: 1;
  border-radius: 8px;
  background: #f6f7f9;

  :deep(.van-field__control) {
    color: #323233;
    -webkit-text-fill-color: #323233;
    caret-color: #323233;
  }

  :deep(.van-field__control::placeholder) {
    color: #969799;
    -webkit-text-fill-color: #969799;
  }
}

.send-button {
  flex: 0 0 58px;
  height: 36px;
  border-radius: 8px;
}
</style>
