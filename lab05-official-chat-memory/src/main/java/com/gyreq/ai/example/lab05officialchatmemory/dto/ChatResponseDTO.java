package com.gyreq.ai.example.lab05officialchatmemory.dto;

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
     *
     * @param content AI 回复内容
     * @param sessionId 会话 ID
     * @param messageCount 历史消息条数
     * @return 响应 DTO
     */
    public static ChatResponseDTO of(String content, String sessionId, int messageCount) {
        return new ChatResponseDTO(content, sessionId, messageCount, System.currentTimeMillis());
    }
}
