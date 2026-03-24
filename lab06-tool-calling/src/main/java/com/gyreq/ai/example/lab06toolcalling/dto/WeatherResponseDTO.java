package com.gyreq.ai.example.lab06toolcalling.dto;

/**
 * 天气查询响应 DTO
 *
 * @param question 用户问题
 * @param answer   AI 回复（可能包含工具调用结果）
 * @author gyreq
 * @since 1.0.0
 */
public record WeatherResponseDTO(
        String question,
        String answer
) {

    /**
     * 静态工厂方法
     *
     * @param question 用户问题
     * @param answer   AI 回复
     * @return WeatherResponseDTO 实例
     */
    public static WeatherResponseDTO of(String question, String answer) {
        return new WeatherResponseDTO(question, answer);
    }

}
