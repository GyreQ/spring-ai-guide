package com.gyreq.ai.example.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.mockito.Mockito.*;

/**
 * Service 层测试基类
 *
 * <p>提供 Mock ChatClient 的通用方法，简化测试代码。
 * 继承此类后，子类可直接使用已配置好的 mock 对象。
 *
 * <p>使用示例：
 * <pre>
 * class MyServiceTest extends BaseChatClientTest {
 *
 *     @Test
 *     void testMyMethod() {
 *         mockCallResponse("expected response");
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @author gyreq
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseChatClientTest {

    @Mock
    protected ChatClient chatClient;

    @Mock
    protected ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    protected ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    protected ChatClient.StreamResponseSpec streamResponseSpec;

    /**
     * 配置 ChatClient 的同步调用 Mock 链
     *
     * @param response AI 返回的内容
     */
    protected void mockCallResponse(String response) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(response);
    }

    /**
     * 配置 ChatClient 的流式调用 Mock 链
     *
     * @param response AI 返回的流式内容
     */
    protected void mockStreamResponse(reactor.core.publisher.Flux<String> response) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(response);
    }

    /**
     * 重置 Mock 状态
     */
    @BeforeEach
    void resetMocks() {
        // Mockito 会自动重置 @Mock 字段，此处可添加额外初始化逻辑
    }

}
