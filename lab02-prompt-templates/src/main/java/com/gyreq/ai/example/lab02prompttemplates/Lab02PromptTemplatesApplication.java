package com.gyreq.ai.example.lab02prompttemplates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab02 - Prompt Templates 应用程序入口
 *
 * <p>本模块演示 Spring AI 的提示词模板功能，包括：
 * <ul>
 *     <li>使用 PromptTemplate 外部化管理提示词</li>
 *     <li>模板文件与代码解耦</li>
 *     <li>动态参数填充</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.gyreq.ai.example")
public class Lab02PromptTemplatesApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab02PromptTemplatesApplication.class, args);
    }

}
