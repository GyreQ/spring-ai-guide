package com.gyreq.ai.example.lab01helloworld.service.impl;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab01helloworld.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab01helloworld.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 聊天服务实现类
 *
 * <p>基于 Spring AI 的 {@link ChatClient} 实现与大模型的交互。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final AiProperties aiProperties;

    @Override
    public ChatResponseDTO chat(String message) {
        log.info("收到用户消息: {}", message);

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        log.info("AI 回复: {}", response);

        return ChatResponseDTO.of(response, aiProperties.getModel());
    }

    @Override
    public Flux<String> chatStream(String message) {
        log.info("收到用户消息(流式): {}", message);

        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

}
