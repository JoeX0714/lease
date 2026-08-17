export interface AiChatRequest {
  sessionId: string;
  message: string;
}

export interface AiChatResponse {
  content: string;
}

export interface AiHistoryMessage {
  role: "USER" | "ASSISTANT";
  content: string;
}
