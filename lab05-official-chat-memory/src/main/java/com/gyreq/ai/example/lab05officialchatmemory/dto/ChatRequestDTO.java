package com.gyreq.ai.example.lab05officialchatmemory.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天请求 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record ChatRequestDTO(

        /**
         * 会话 ID，用于标识多轮对话
         */
        @NotBlank(message = "会话 ID 不能为空")
        String sessionId,

        /**
         * 用户消息内容
         */
        @NotBlank(message = "消息内容不能为空")
        String content

) {
}
