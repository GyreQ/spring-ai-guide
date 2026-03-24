package com.gyreq.ai.example.lab01helloworld.dto;

/**
 * 聊天响应 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record ChatResponseDTO(

        /**
         * AI 模型返回的回复内容
         */
        String content,

        /**
         * 使用的模型名称
         */
        String model,

        /**
         * 响应时间戳
         */
        long timestamp

) {
    /**
     * 创建成功响应
     *
     * @param content AI 回复内容
     * @param model   模型名称
     * @return 响应 DTO
     */
    public static ChatResponseDTO of(String content, String model) {
        return new ChatResponseDTO(content, model, System.currentTimeMillis());
    }
}
