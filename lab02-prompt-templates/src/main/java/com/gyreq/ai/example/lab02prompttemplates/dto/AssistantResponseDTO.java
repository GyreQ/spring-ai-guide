package com.gyreq.ai.example.lab02prompttemplates.dto;

/**
 * 助手响应 DTO
 *
 * <p>用于封装角色扮演助手的响应结果。
 *
 * @author gyreq
 * @since 1.0.0
 */
public record AssistantResponseDTO(

        /**
         * AI 助手的回复内容
         */
        String content,

        /**
         * 助手名字
         */
        String name,

        /**
         * 助手风格
         */
        String voice,

        /**
         * 用户的问题
         */
        String question,

        /**
         * 响应时间戳
         */
        long timestamp

) {
    /**
     * 创建成功响应
     *
     * @param content  回复内容
     * @param name     助手名字
     * @param voice    助手风格
     * @param question 用户问题
     * @return 响应 DTO
     */
    public static AssistantResponseDTO of(String content, String name, String voice, String question) {
        return new AssistantResponseDTO(content, name, voice, question, System.currentTimeMillis());
    }
}
