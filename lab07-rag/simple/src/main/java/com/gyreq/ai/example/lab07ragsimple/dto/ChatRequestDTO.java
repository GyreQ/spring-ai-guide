package com.gyreq.ai.example.lab07ragsimple.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天请求 DTO
 *
 * @param question 用户问题
 * @author gyreq
 * @since 1.0.0
 */
public record ChatRequestDTO(

        /**
         * 用户问题
         */
        @NotBlank(message = "问题不能为空")
        String question

) {

}
