package com.gyreq.ai.example.lab01helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab01 - Spring AI Hello World 应用程序入口
 *
 * <p>本模块演示 Spring AI 的基本用法，包括：
 * <ul>
 *     <li>使用 ChatClient 进行对话</li>
 *     <li>同步调用大模型</li>
 *     <li>流式调用大模型（SSE）</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.gyreq.ai.example")
public class Lab01HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab01HelloworldApplication.class, args);
    }

}
