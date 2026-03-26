package com.gyreq.ai.example.lab07ragpg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab07 RAG PgVector 启动类
 *
 * <p>本模块演示使用 PostgreSQL PgVector 实现 RAG 检索增强生成。
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootApplication
public class Lab07RagPgApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab07RagPgApplication.class, args);
    }

}
