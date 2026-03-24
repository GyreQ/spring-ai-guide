package com.gyreq.ai.example.lab04chatmemory.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JdbcChatMemoryStore 单元测试
 *
 * <p>使用 H2 内存数据库进行测试。
 *
 * @author gyreq
 * @since 1.0.0
 */
@JdbcTest
@Sql(scripts = "/test-schema.sql")
class JdbcChatMemoryStoreTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcChatMemoryStore store;

    @BeforeEach
    void setUp() {
        store = new JdbcChatMemoryStore(jdbcTemplate);
    }

    @Nested
    @DisplayName("消息存取测试")
    class AddAndGetTest {

        @Test
        @DisplayName("应正确添加和获取消息")
        void shouldAddAndGetMessages() {
            // given
            String conversationId = "jdbc-conversation-1";
            List<Message> messages = List.of(
                    new UserMessage("你好"),
                    new AssistantMessage("你好！")
            );

            // when
            store.add(conversationId, messages);
            List<Message> result = store.get(conversationId);

            // then
            assertEquals(2, result.size());
            assertEquals("你好", result.get(0).getText());
            assertEquals("你好！", result.get(1).getText());
        }
    }

    @Nested
    @DisplayName("会话隔离测试")
    class ConversationIsolationTest {

        @Test
        @DisplayName("不同会话的消息应相互隔离")
        void shouldIsolateDifferentConversations() {
            // given
            String conversation1 = "jdbc-conversation-a";
            String conversation2 = "jdbc-conversation-b";
            store.add(conversation1, List.of(new UserMessage("会话A")));
            store.add(conversation2, List.of(new UserMessage("会话B")));

            // when
            List<Message> result1 = store.get(conversation1);
            List<Message> result2 = store.get(conversation2);

            // then
            assertEquals(1, result1.size());
            assertEquals("会话A", result1.get(0).getText());
            assertEquals(1, result2.size());
            assertEquals("会话B", result2.get(0).getText());
        }
    }

    @Nested
    @DisplayName("清除记忆测试")
    class ClearTest {

        @Test
        @DisplayName("清除后应返回空列表")
        void shouldReturnEmptyAfterClear() {
            // given
            String conversationId = "jdbc-conversation-clear";
            store.add(conversationId, List.of(new UserMessage("测试")));

            // when
            store.clear(conversationId);
            List<Message> result = store.get(conversationId);

            // then
            assertTrue(result.isEmpty());
        }
    }

}
