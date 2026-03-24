package com.gyreq.ai.example.lab03structuredoutput.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 信息提取请求 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record ExtractRequestDTO(

        /**
         * 待提取的自然语言文本
         */
        @NotBlank(message = "文本内容不能为空")
        String text

) {
}
