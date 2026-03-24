package com.gyreq.ai.example.lab04chatmemory.memory;

import com.gyreq.ai.example.common.memory.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 内存聊天记忆存储
 *
 * <p>使用 {@link ConcurrentHashMap} 实现，适用于开发测试环境。
 *
 * <p>条件装配：当配置 spring.ai.chat.memory.type=memory 时生效（默认）。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(
        name = "spring.ai.chat.memory.type",
        havingValue = "memory",
        matchIfMissing = true
)
public class InMemoryChatMemoryStore implements ChatMemory, ChatMemoryStore {

    private final Map<String, List<Message>> memory = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<Message> messages) {
        log.debug("追加消息到会话 {}: {} 条消息", conversationId, messages.size());
        memory.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>())
                .addAll(messages);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> messages = memory.get(conversationId);
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(messages);
    }

    @Override
    public void clear(String conversationId) {
        log.debug("清除会话 {} 的记忆", conversationId);
        memory.remove(conversationId);
    }

}
