package com.gyreq.ai.example.common.config;

import com.gyreq.ai.example.common.memory.ChatMemoryProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 *
 * <p>配置 Spring AI 的核心 Bean。
 * 所有 Lab 模块通过引入 guide-common 自动获得 ChatClient Bean。
 *
 * <p>注意：Spring AI OpenAI Starter 会自动配置 {@link org.springframework.ai.chat.model.ChatModel} Bean，
 * Lab 模块可直接注入 ChatModel 使用，无需额外配置。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({AiProperties.class, ChatMemoryProperties.class})
public class ChatClientConfig {

    /**
     * 创建 ChatClient Bean
     *
     * <p>ChatClient 是 Spring AI 提供的高级 API，用于与大模型进行交互。
     * 它封装了底层的 ChatModel，提供了流畅的链式调用接口。
     *
     * <p>使用 @ConditionalOnMissingBean 允许 Lab 模块覆盖此配置，
     * 例如 lab04-chat-memory 需要配置带记忆功能的 ChatClient。
     *
     * @param builder ChatClient 构建器，由 Spring 自动注入
     * @return ChatClient 实例
     */
    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

}
