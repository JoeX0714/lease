import http from "@/utils/http";
import type { AiChatRequest, AiChatResponse, AiHistoryMessage } from "@/api/ai/types";

export function chatWithAi(data: AiChatRequest) {
  return http.post<AiChatResponse>("/app/ai/chat", data, {
    timeout: 60000
  });
}

export function getAiHistory(sessionId: string) {
  return http.get<AiHistoryMessage[]>(
    "/app/ai/history",
    { sessionId },
    { silent: true }
  );
}
