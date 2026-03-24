package com.gyreq.ai.example.lab04chatmemory.service.impl;

import com.gyreq.ai.example.lab04chatmemory.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab04chatmemory.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

/**
 * 聊天服务实现类
 *
 * <p>使用 Spring AI Advisor 模式实现带记忆功能的聊天服务。
 * Advisor 自动完成历史消息的加载和保存。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    /**
     * 会话 ID 参数键名
     */
    private static final String CONVERSATION_ID_KEY = "chat_memory_conversation_id";

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Override
    public ChatResponseDTO chat(String sessionId, String content) {
        log.info("收到聊天请求: sessionId={}, content={}", sessionId, content);

        String response = chatClient.prompt()
                .user(content)
                .advisors(spec -> spec.param(CONVERSATION_ID_KEY, sessionId))
                .call()
                .content();

        log.info("AI 回复: {}", response);

        int messageCount = chatMemory.get(sessionId).size();

        return ChatResponseDTO.of(response, sessionId, messageCount);
    }

}
