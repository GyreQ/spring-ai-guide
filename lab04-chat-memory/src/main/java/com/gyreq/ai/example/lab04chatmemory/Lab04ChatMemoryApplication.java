package com.gyreq.ai.example.lab04chatmemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab04 - Chat Memory 应用程序入口
 *
 * <p>本模块演示 Spring AI 的对话记忆功能，包括：
 * <ul>
 *     <li>策略模式实现多种存储后端</li>
 *     <li>条件装配实现存储切换</li>
 *     <li>生产级架构设计</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.gyreq.ai.example")
public class Lab04ChatMemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab04ChatMemoryApplication.class, args);
    }

}
