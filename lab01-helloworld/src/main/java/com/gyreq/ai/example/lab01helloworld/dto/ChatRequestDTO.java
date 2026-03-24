package com.gyreq.ai.example.lab01helloworld.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天请求 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record ChatRequestDTO(

        /**
         * 用户消息内容
         */
        @NotBlank(message = "消息内容不能为空")
        String message

) {
}
