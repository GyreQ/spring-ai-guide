package com.gyreq.ai.example.lab03structuredoutput.dto;

import com.gyreq.ai.example.lab03structuredoutput.model.PersonInfo;

/**
 * 信息提取响应 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record ExtractResponseDTO(

        /**
         * 提取出的人员信息
         */
        PersonInfo personInfo,

        /**
         * 原始输入文本
         */
        String originalText,

        /**
         * 响应时间戳
         */
        long timestamp

) {
    /**
     * 创建成功响应
     *
     * @param personInfo  人员信息
     * @param originalText 原始文本
     * @return 响应 DTO
     */
    public static ExtractResponseDTO of(PersonInfo personInfo, String originalText) {
        return new ExtractResponseDTO(personInfo, originalText, System.currentTimeMillis());
    }
}
