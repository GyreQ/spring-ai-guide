package com.gyreq.ai.example.lab05officialchatmemory.service.impl;

import com.gyreq.ai.example.lab05officialchatmemory.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab05officialchatmemory.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

/**
 * 聊天服务实现类
 *
 * <p>使用 Spring AI 官方组件实现带记忆功能的聊天服务。
 * 业务代码极其简洁，无需手动操作 Repository。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Override
    public ChatResponseDTO chat(String sessionId, String content) {
        log.info("收到聊天请求: sessionId={}, content={}", sessionId, content);

        String response = chatClient.prompt()
                .user(content)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();

        log.info("AI 回复: {}", response);

        int messageCount = chatMemory.get(sessionId).size();

        return ChatResponseDTO.of(response, sessionId, messageCount);
    }

}
