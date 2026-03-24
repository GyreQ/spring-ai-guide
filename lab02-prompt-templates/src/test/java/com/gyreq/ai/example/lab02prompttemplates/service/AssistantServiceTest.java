package com.gyreq.ai.example.lab02prompttemplates.service;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab02prompttemplates.dto.AssistantResponseDTO;
import com.gyreq.ai.example.lab02prompttemplates.exception.TemplateResourceException;
import com.gyreq.ai.example.lab02prompttemplates.service.impl.AssistantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AssistantService 单元测试
 *
 * <p>测试 SystemPromptTemplate 的使用场景，验证系统消息与用户消息的组合。
 *
 * @author gyreq
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private AiProperties aiProperties;

    @Mock
    private Resource systemAssistantTemplateResource;

    private AssistantService assistantService;

    @BeforeEach
    void setUp() throws Exception {
        // 配置模板资源的默认行为
        String templateContent = "You are a helpful AI assistant.\nYour name is {name}.\nYou should reply in the style of a {voice}.";
        when(systemAssistantTemplateResource.exists()).thenReturn(true);
        when(systemAssistantTemplateResource.getContentAsString(StandardCharsets.UTF_8))
                .thenReturn(templateContent);

        // 创建服务实例
        assistantService = new AssistantServiceImpl(chatModel, aiProperties, systemAssistantTemplateResource);
    }

    @Nested
    @DisplayName("角色扮演对话测试")
    class ChatTest {

        @Test
        @DisplayName("正常参数时应成功返回助手回复")
        void shouldReturnResponse_whenValidParams() throws Exception {
            // given
            String name = "Jarvis";
            String voice = "莎士比亚";
            String question = "你好，请介绍一下自己";
            String expectedResponse = "吾乃 Jarvis，一位以莎士比亚风格言说之助手...";

            mockChatModelResponse(expectedResponse);

            // when
            AssistantResponseDTO response = assistantService.chat(name, voice, question);

            // then
            assertNotNull(response);
            assertEquals(expectedResponse, response.content());
            assertEquals(name, response.name());
            assertEquals(voice, response.voice());
            assertEquals(question, response.question());
            assertTrue(response.timestamp() > 0);
        }

        @Test
        @DisplayName("系统消息应包含助手名字")
        void shouldIncludeNameInSystemMessage_whenChat() throws Exception {
            // given
            String name = "Alice";
            String voice = "科幻小说家";
            String question = "未来是什么样的？";

            mockChatModelResponse("回复内容");

            // when
            assistantService.chat(name, voice, question);

            // then: 验证调用时传入的 Prompt 包含系统消息
            verify(chatModel).call(any(Prompt.class));
        }

        @Test
        @DisplayName("应正确构建包含系统消息和用户消息的 Prompt")
        void shouldBuildCorrectPrompt_whenChat() throws Exception {
            // given
            String name = "Bob";
            String voice = "幽默大师";
            String question = "讲个笑话";

            mockChatModelResponse("笑话内容");

            // when
            assistantService.chat(name, voice, question);

            // then: 验证 ChatModel.call 被调用，且传入的是 Prompt 对象
            verify(chatModel).call(argThat((Prompt prompt) -> {
                // Prompt 应包含两条消息：系统消息 + 用户消息
                List<?> instructions = prompt.getInstructions();
                return instructions.size() == 2;
            }));
        }
    }

    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("模板文件不存在时应抛出 TemplateResourceException")
        void shouldThrowException_whenTemplateNotExists() throws Exception {
            // given
            when(systemAssistantTemplateResource.exists()).thenReturn(false);

            // 重新创建服务实例
            assistantService = new AssistantServiceImpl(chatModel, aiProperties, systemAssistantTemplateResource);

            // when & then
            TemplateResourceException exception = assertThrows(
                    TemplateResourceException.class,
                    () -> assistantService.chat("Jarvis", "莎士比亚", "你好")
            );
            assertTrue(exception.getMessage().contains("模板文件不存在"));
        }

        @Test
        @DisplayName("读取模板文件失败时应抛出 TemplateResourceException")
        void shouldThrowException_whenTemplateReadFailed() throws Exception {
            // given
            when(systemAssistantTemplateResource.exists()).thenReturn(true);
            when(systemAssistantTemplateResource.getContentAsString(StandardCharsets.UTF_8))
                    .thenThrow(new RuntimeException("IO异常"));

            // 重新创建服务实例
            assistantService = new AssistantServiceImpl(chatModel, aiProperties, systemAssistantTemplateResource);

            // when & then
            TemplateResourceException exception = assertThrows(
                    TemplateResourceException.class,
                    () -> assistantService.chat("Jarvis", "莎士比亚", "你好")
            );
            assertTrue(exception.getMessage().contains("读取模板文件失败"));
        }
    }

    /**
     * 配置 ChatModel 的 Mock 响应
     *
     * @param response AI 返回的内容
     */
    private void mockChatModelResponse(String response) {
        AssistantMessage assistantMessage = new AssistantMessage(response);
        Generation generation = new Generation(assistantMessage);
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
    }
}
