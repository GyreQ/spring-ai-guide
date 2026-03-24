package com.gyreq.ai.example.lab03structuredoutput;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab03 - Structured Output 应用程序入口
 *
 * <p>本模块演示 Spring AI 的结构化输出功能，包括：
 * <ul>
 *     <li>使用 BeanOutputConverter 实现结构化输出</li>
 *     <li>JSON Schema 自动生成</li>
 *     <li>POJO 自动映射</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.gyreq.ai.example")
public class Lab03StructuredOutputApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab03StructuredOutputApplication.class, args);
    }

}
