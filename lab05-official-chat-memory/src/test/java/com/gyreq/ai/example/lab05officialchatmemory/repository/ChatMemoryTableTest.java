package com.gyreq.ai.example.lab05officialchatmemory.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库表结构测试
 *
 * <p>验证 JdbcChatMemory 自动创建的表结构。
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class ChatMemoryTableTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("验证 chat_memory 表已创建")
    void shouldCreateTable_whenApplicationStarts() {
        // 查询表是否存在（H2 数据库）
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_NAME = 'CHAT_MEMORY'",
                Integer.class
        );

        assertEquals(1, count, "CHAT_MEMORY 表应该被自动创建");
    }

    @Test
    @DisplayName("验证表结构正确")
    void shouldHaveCorrectTableStructure() {
        // 插入测试数据
        jdbcTemplate.update(
                "INSERT INTO chat_memory (conversation_id, message_type, content) VALUES (?, ?, ?)",
                "test-conv", "USER", "test content"
        );

        // 查询验证
        String content = jdbcTemplate.queryForObject(
                "SELECT content FROM chat_memory WHERE conversation_id = ?",
                String.class,
                "test-conv"
        );

        assertEquals("test content", content);
    }

}
