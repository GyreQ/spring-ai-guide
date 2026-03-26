package com.gyreq.ai.example.lab07ragsimple;

import com.gyreq.ai.example.common.config.AiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Lab07 RAG Simple VectorStore 启动类
 *
 * <p>本模块演示使用 SimpleVectorStore 实现 RAG 检索增强生成。
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication
@EnableConfigurationProperties(AiProperties.class)
public class Lab07RagSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab07RagSimpleApplication.class, args);
    }

}
