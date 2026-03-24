package com.gyreq.ai.example.lab04chatmemory.memory;

import com.gyreq.ai.example.common.memory.ChatMemoryProperties;
import com.gyreq.ai.example.common.memory.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 聊天记忆存储
 *
 * <p>使用 {@link RedisTemplate} 实现，适用于高并发分布式场景。
 *
 * <p>条件装配：当配置 spring.ai.chat.memory.type=redis 时生效。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(
        name = "spring.ai.chat.memory.type",
        havingValue = "redis"
)
public class RedisChatMemoryStore implements ChatMemory, ChatMemoryStore {

    private static final String TYPE_USER = "user";
    private static final String TYPE_ASSISTANT = "assistant";
    private static final String TYPE_SYSTEM = "system";

    private static final long DEFAULT_TTL_DAYS = 7;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMemoryProperties properties;

    public RedisChatMemoryStore(RedisTemplate<String, Object> redisTemplate,
                                ChatMemoryProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = buildKey(conversationId);
        log.debug("追加消息到 Redis 会话 {}: {} 条消息", conversationId, messages.size());

        Object[] messageMaps = messages.stream()
                .map(this::messageToMap)
                .toArray();

        redisTemplate.opsForList().rightPushAll(key, messageMaps);
        redisTemplate.expire(key, DEFAULT_TTL_DAYS, TimeUnit.DAYS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> get(String conversationId) {
        String key = buildKey(conversationId);
        Long size = redisTemplate.opsForList().size(key);

        if (size == null || size == 0) {
            return new ArrayList<>();
        }

        List<Object> objects = redisTemplate.opsForList().range(key, 0, size - 1);
        if (objects == null) {
            return new ArrayList<>();
        }

        return objects.stream()
                .map(obj -> (Map<String, String>) obj)
                .map(this::mapToMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        String key = buildKey(conversationId);
        log.debug("清除 Redis 会话 {} 的记忆", conversationId);
        redisTemplate.delete(key);
    }

    private String buildKey(String conversationId) {
        return properties.getRedisKeyPrefix() + conversationId;
    }

    private Map<String, String> messageToMap(Message message) {
        String type;
        if (message instanceof UserMessage) {
            type = TYPE_USER;
        } else if (message instanceof AssistantMessage) {
            type = TYPE_ASSISTANT;
        } else if (message instanceof SystemMessage) {
            type = TYPE_SYSTEM;
        } else {
            type = TYPE_USER;
        }

        return Map.of(
                "type", type,
                "content", message.getText()
        );
    }

    private Message mapToMessage(Map<String, String> map) {
        String type = map.get("type");
        String content = map.get("content");

        return switch (type) {
            case TYPE_USER -> new UserMessage(content);
            case TYPE_ASSISTANT -> new AssistantMessage(content);
            case TYPE_SYSTEM -> new SystemMessage(content);
            default -> new UserMessage(content);
        };
    }

}
