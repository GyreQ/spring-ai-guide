package com.gyreq.ai.example.lab07ragsimple.dto;

import com.gyreq.ai.example.common.config.AiProperties;

/**
 * 聊天响应 DTO
 *
 * @param content AI 回答内容
 * @param model   使用的模型名称
 * @author gyreq
 * @since 1.0.0
 */
public record ChatResponseDTO(

        /**
         * AI 回答内容
         */
        String content,

        /**
         * 使用的模型名称
         */
        String model

) {

    /**
     * 创建响应 DTO
     *
     * @param content      AI 回答内容
     * @param aiProperties AI 配置属性
     * @return 响应 DTO
     */
    public static ChatResponseDTO of(String content, AiProperties aiProperties) {
        return new ChatResponseDTO(content, aiProperties.getModel());
    }

}
