package com.gyreq.ai.example.lab04chatmemory.memory;

import com.gyreq.ai.example.common.memory.ChatMemoryProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisChatMemoryStore 单元测试
 *
 * <p>使用 Mockito 模拟 RedisTemplate。
 *
 * @author gyreq
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RedisChatMemoryStoreTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOperations;

    private RedisChatMemoryStore store;
    private ChatMemoryProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ChatMemoryProperties();
        properties.setRedisKeyPrefix("chat:memory:");
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        store = new RedisChatMemoryStore(redisTemplate, properties);
    }

    @Nested
    @DisplayName("消息添加测试")
    class AddTest {

        @Test
        @DisplayName("应正确添加消息到 Redis")
        void shouldAddToRedis() {
            // given
            String conversationId = "redis-conversation-1";
            List<Message> messages = List.of(
                    new UserMessage("你好"),
                    new AssistantMessage("你好！")
            );

            // when
            store.add(conversationId, messages);

            // then
            verify(listOperations).rightPushAll(eq("chat:memory:redis-conversation-1"), any(Object[].class));
            verify(redisTemplate).expire(eq("chat:memory:redis-conversation-1"), anyLong(), any());
        }
    }

    @Nested
    @DisplayName("消息获取测试")
    class GetTest {

        @Test
        @DisplayName("Redis 中无数据时返回空列表")
        void shouldReturnEmptyWhenNoData() {
            // given
            String conversationId = "empty-conversation";
            when(listOperations.size(anyString())).thenReturn(0L);

            // when
            List<Message> result = store.get(conversationId);

            // then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Redis 中无数据时 size 为 null 返回空列表")
        void shouldReturnEmptyWhenSizeIsNull() {
            // given
            String conversationId = "null-conversation";
            when(listOperations.size(anyString())).thenReturn(null);

            // when
            List<Message> result = store.get(conversationId);

            // then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("清除记忆测试")
    class ClearTest {

        @Test
        @DisplayName("清除时应删除 Redis Key")
        void shouldDeleteRedisKey() {
            // given
            String conversationId = "conversation-to-clear";

            // when
            store.clear(conversationId);

            // then
            verify(redisTemplate).delete("chat:memory:conversation-to-clear");
        }
    }

}
