package com.gyreq.ai.example.lab02prompttemplates.dto;

/**
 * 笑话响应 DTO
 *
 * @author gyreq
 * @since 1.0.0
 */
public record JokeResponseDTO(

        /**
         * 笑话内容
         */
        String joke,

        /**
         * 笑话主角名字
         */
        String name,

        /**
         * 笑话主题
         */
        String topic,

        /**
         * 响应时间戳
         */
        long timestamp

) {
    /**
     * 创建成功响应
     *
     * @param joke  笑话内容
     * @param name  主角名字
     * @param topic 主题
     * @return 响应 DTO
     */
    public static JokeResponseDTO of(String joke, String name, String topic) {
        return new JokeResponseDTO(joke, name, topic, System.currentTimeMillis());
    }
}
