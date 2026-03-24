package com.gyreq.ai.example.lab06toolcalling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab06 - Tool Calling 演示应用
 *
 * <p>本模块演示 Spring AI 的工具调用（Function Calling）能力，
 * 让 AI 模型能够调用外部工具扩展其能力边界。
 *
 * <p>核心知识点：
 * <ul>
 *   <li>{@code @Tool} 注解定义工具方法</li>
 *   <li>{@code @ToolParam} 注解定义参数描述</li>
 *   <li>ChatClient 注册默认工具</li>
 *   <li>工具执行异常处理机制</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication
public class Lab06ToolCallingApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab06ToolCallingApplication.class, args);
    }

}
