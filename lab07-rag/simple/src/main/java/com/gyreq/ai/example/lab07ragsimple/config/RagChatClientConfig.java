package com.gyreq.ai.example.lab07ragsimple.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG ChatClient 配置类
 *
 * <p>配置带有 QuestionAnswerAdvisor 的 ChatClient，
 * 实现 RAG（检索增强生成）功能。
 *
 * <p>QuestionAnswerAdvisor 是 Spring AI 提供的 RAG 实现，
 * 它会自动：
 * <ol>
 *   <li>将用户问题在向量库中进行相似度搜索</li>
 *   <li>将检索到的相关文档作为上下文注入到 Prompt 中</li>
 *   <li>让模型基于上下文生成回答</li>
 * </ol>
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
     * @param vectorStore 向量存储
     * @return 配置了 RAG 能力的 ChatClient
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore) {
        return builder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

}
