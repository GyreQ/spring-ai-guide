package com.gyreq.ai.example.lab05officialchatmemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab05 应用启动类
 *
 * <p>演示 Spring AI 官方提供的对话记忆自动配置方案。
 * 使用 spring-ai-starter-model-chat-memory-repository-jdbc 实现。
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication
public class Lab05OfficialChatMemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab05OfficialChatMemoryApplication.class, args);
    }

}
