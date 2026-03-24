package com.gyreq.ai.example.lab01helloworld.service;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.common.test.BaseChatClientTest;
import com.gyreq.ai.example.lab01helloworld.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab01helloworld.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ChatService 单元测试
 *
 * <p>继承 BaseChatClientTest，复用 ChatClient 的 Mock 配置。
 *
 * @author gyreq
 * @since 1.0.0
 */
class ChatServiceTest extends BaseChatClientTest {

    @Mock
    private AiProperties aiProperties;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        // 配置 AiProperties 的默认行为
        when(aiProperties.getModel()).thenReturn("test-model");

        // 手动创建 ChatServiceImpl 实例，注入 Mock 的依赖
        chatService = new ChatServiceImpl(chatClient, aiProperties);
    }

    @Nested
    @DisplayName("同步聊天测试")
    class ChatTest {

        @Test
        @DisplayName("发送正常消息时应返回 AI 回复")
        void shouldReturnAiResponse_whenSendMessage() {
            // given
            String userMessage = "你好，介绍一下 Spring AI";
            String expectedResponse = "Spring AI 是 Spring 生态系统中用于集成 AI 模型的框架...";
            String expectedModel = "test-model";

            // 使用基类方法配置 Mock 链
            mockCallResponse(expectedResponse);

            // when
            ChatResponseDTO response = chatService.chat(userMessage);

            // then
            assertNotNull(response);
            assertEquals(expectedResponse, response.content());
            assertEquals(expectedModel, response.model());
            assertTrue(response.timestamp() > 0);

            // 验证调用
            verify(chatClient).prompt();
            verify(requestSpec).user(userMessage);
            verify(requestSpec).call();
            verify(aiProperties).getModel();
        }

        @Test
        @DisplayName("发送空消息时应正常处理")
        void shouldHandleEmptyMessage_whenSendEmptyMessage() {
            // given
            String emptyMessage = "";
            String expectedResponse = "请提供您的问题。";

            mockCallResponse(expectedResponse);

            // when
            ChatResponseDTO response = chatService.chat(emptyMessage);

            // then
            assertNotNull(response);
            assertEquals(expectedResponse, response.content());
        }

        @Test
        @DisplayName("响应应使用配置中的模型名称")
        void shouldUseConfiguredModel_whenChat() {
            // given
            String configuredModel = "gpt-4o";
            when(aiProperties.getModel()).thenReturn(configuredModel);
            mockCallResponse("response");

            // when
            ChatResponseDTO response = chatService.chat("test");

            // then
            assertEquals(configuredModel, response.model());
        }
    }

    @Nested
    @DisplayName("流式聊天测试")
    class ChatStreamTest {

        @Test
        @DisplayName("流式聊天应返回 Flux 流")
        void shouldReturnFlux_whenChatStream() {
            // given
            String userMessage = "写一首诗";
            Flux<String> expectedFlux = Flux.just("春", "眠", "不", "觉", "晓");

            mockStreamResponse(expectedFlux);

            // when
            Flux<String> result = chatService.chatStream(userMessage);

            // then
            StepVerifier.create(result)
                    .expectNext("春", "眠", "不", "觉", "晓")
                    .verifyComplete();

            verify(chatClient).prompt();
            verify(requestSpec).user(userMessage);
            verify(requestSpec).stream();
        }

        @Test
        @DisplayName("流式聊天应正确处理错误")
        void shouldHandleError_whenChatStreamError() {
            // given
            Flux<String> errorFlux = Flux.error(new RuntimeException("API 调用失败"));

            mockStreamResponse(errorFlux);

            // when
            Flux<String> result = chatService.chatStream("test");

            // then
            StepVerifier.create(result)
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

}
