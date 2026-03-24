package com.gyreq.ai.example.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 模型配置属性
 *
 * <p>从 application.yml 中读取 spring.ai.openai.chat.options 前缀的配置。
 *
 * @author gyreq
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.ai.openai.chat.options")
public class AiProperties {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 温度参数（控制输出随机性，0-2，值越小越确定）
     */
    private Double temperature;

    /**
     * 最大输出 token 数
     */
    private Integer maxTokens;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

}
