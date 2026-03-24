package com.gyreq.ai.example.lab05officialchatmemory.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 官方对话记忆配置类
 *
 * <p>使用 Spring AI 官方提供的 ChatMemory 接口。
 * ChatMemory 的实现由 JdbcChatMemory 类提供（使用数据库持久化）。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Configuration
public class OfficialMemoryConfig {

    /**
     * 配置带记忆功能的 ChatClient
     *
     * <p>注入 MessageChatMemoryAdvisor，自动管理对话记忆。
     * 此配置覆盖 guide-common 中的默认 ChatClient Bean。
     *
     * @param chatModel AI 模型
     * @param chatMemory 对话记忆（由 JdbcChatMemory 提供）
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

}
