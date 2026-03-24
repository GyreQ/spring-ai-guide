package com.gyreq.ai.example.lab04chatmemory.dto;

/**
 * 聊天响应 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record ChatResponseDTO(

        /**
         * AI 回复内容
         */
        String content,

        /**
         * 会话 ID
         */
        String sessionId,

        /**
         * 当前历史消息条数
         */
        int messageCount,

        /**
         * 响应时间戳
         */
        long timestamp

) {
    /**
     * 创建成功响应
     */
    public static ChatResponseDTO of(String content, String sessionId, int messageCount) {
        return new ChatResponseDTO(content, sessionId, messageCount, System.currentTimeMillis());
    }
}
