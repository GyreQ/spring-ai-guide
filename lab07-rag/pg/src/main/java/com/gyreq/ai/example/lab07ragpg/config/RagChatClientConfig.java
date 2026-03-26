package com.gyreq.ai.example.lab07ragpg.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG ChatClient 配置类
 *
 * <p>配置带有 QuestionAnswerAdvisor 的 ChatClient，
 * 实现 RAG（检索增强生成）功能。
 *
 * <p>与 simple 模块不同，PgVector 的 VectorStore 由 Spring AI 自动配置，
 * 无需手动创建 Bean。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Configuration
public class RagChatClientConfig {

    /**
     * 创建带 QuestionAnswerAdvisor 的 ChatClient Bean
     *
     * <p>覆盖 guide-common 中的默认 ChatClient 配置，
     * 添加 RAG 能力。
     *
     * @param builder     ChatClient 构建器
     * @param vectorStore 向量存储（由 Spring AI 自动配置）
     * @return 配置了 RAG 能力的 ChatClient
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore) {
        return builder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

}
