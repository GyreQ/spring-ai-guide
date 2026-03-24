package com.gyreq.ai.example.lab04chatmemory.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 带记忆功能的 ChatClient 配置类
 *
 * <p>配置带记忆功能的 ChatClient，使用 Spring AI 的 Advisor 模式。
 * 此配置会覆盖 guide-common 中的默认 ChatClient Bean。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Configuration
public class MemoryChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

}
