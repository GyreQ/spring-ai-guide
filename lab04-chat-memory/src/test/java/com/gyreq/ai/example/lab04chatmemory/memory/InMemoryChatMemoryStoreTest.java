package com.gyreq.ai.example.lab04chatmemory.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InMemoryChatMemoryStore 单元测试
 *
 * @author gyreq
 * @since 1.0.0
 */
class InMemoryChatMemoryStoreTest {

    private InMemoryChatMemoryStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryChatMemoryStore();
    }

    @Nested
    @DisplayName("消息存取测试")
    class AddAndGetTest {

        @Test
        @DisplayName("应正确添加和获取消息")
        void shouldAddAndGetMessages() {
            // given
            String conversationId = "conversation-1";
            List<Message> messages = List.of(
                    new UserMessage("你好"),
                    new AssistantMessage("你好！有什么我可以帮助你的？")
            );

            // when
            store.add(conversationId, messages);
            List<Message> result = store.get(conversationId);

            // then
            assertEquals(2, result.size());
            assertEquals("你好", result.get(0).getText());
            assertEquals("你好！有什么我可以帮助你的？", result.get(1).getText());
        }

        @Test
        @DisplayName("获取不存在的会话应返回空列表")
        void shouldReturnEmptyWhenConversationNotExists() {
            // when
            List<Message> result = store.get("non-existent");

            // then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("会话隔离测试")
    class ConversationIsolationTest {

        @Test
        @DisplayName("不同会话的消息应相互隔离")
        void shouldIsolateDifferentConversations() {
            // given
            String conversation1 = "conversation-a";
            String conversation2 = "conversation-b";
            store.add(conversation1, List.of(new UserMessage("会话A的消息")));
            store.add(conversation2, List.of(new UserMessage("会话B的消息")));

            // when
            List<Message> result1 = store.get(conversation1);
            List<Message> result2 = store.get(conversation2);

            // then
            assertEquals(1, result1.size());
            assertEquals("会话A的消息", result1.get(0).getText());
            assertEquals(1, result2.size());
            assertEquals("会话B的消息", result2.get(0).getText());
        }
    }

    @Nested
    @DisplayName("清除记忆测试")
    class ClearTest {

        @Test
        @DisplayName("清除后应返回空列表")
        void shouldReturnEmptyAfterClear() {
            // given
            String conversationId = "conversation-to-clear";
            store.add(conversationId, List.of(new UserMessage("测试消息")));

            // when
            store.clear(conversationId);
            List<Message> result = store.get(conversationId);

            // then
            assertTrue(result.isEmpty());
        }
    }

}
