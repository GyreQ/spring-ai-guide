package com.gyreq.ai.example.lab02prompttemplates.service;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.common.test.BaseChatClientTest;
import com.gyreq.ai.example.lab02prompttemplates.dto.JokeResponseDTO;
import com.gyreq.ai.example.lab02prompttemplates.exception.TemplateResourceException;
import com.gyreq.ai.example.lab02prompttemplates.service.impl.JokeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JokeService 单元测试
 *
 * <p>继承 BaseChatClientTest，复用 ChatClient 的 Mock 配置。
 *
 * @author gyreq
 * @since 1.0.0
 */
class JokeServiceTest extends BaseChatClientTest {

    @Mock
    private AiProperties aiProperties;

    @Mock
    private Resource jokeTemplateResource;

    private JokeService jokeService;

    @BeforeEach
    void setUp() throws Exception {
        // 配置模板资源的默认行为
        String templateContent = "请用中文讲一个关于 {name} 的笑话，主题是 {topic}。要求风趣幽默，不超过50字。";
        when(jokeTemplateResource.exists()).thenReturn(true);
        when(jokeTemplateResource.getContentAsString(StandardCharsets.UTF_8))
                .thenReturn(templateContent);

        // 创建服务实例
        jokeService = new JokeServiceImpl(chatClient, aiProperties, jokeTemplateResource);
    }

    @Nested
    @DisplayName("生成笑话测试")
    class GenerateJokeTest {

        @Test
        @DisplayName("正常参数时应成功生成笑话")
        void shouldGenerateJoke_whenValidParams() throws Exception {
            // given
            String name = "小明";
            String topic = "程序员";
            String expectedJoke = "小明写代码从不写注释，同事问他为什么，他说：我的代码自解释！";

            mockCallResponse(expectedJoke);

            // when
            JokeResponseDTO response = jokeService.generateJoke(name, topic);

            // then
            assertNotNull(response);
            assertEquals(expectedJoke, response.joke());
            assertEquals(name, response.name());
            assertEquals(topic, response.topic());
            assertTrue(response.timestamp() > 0);
        }

        @Test
        @DisplayName("构造的 Prompt 应包含用户传入的 name")
        void shouldIncludeNameInPrompt_whenGenerateJoke() throws Exception {
            // given
            String name = "张三";
            String topic = "考试";

            mockCallResponse("笑话内容");

            // when
            jokeService.generateJoke(name, topic);

            // then
            verify(requestSpec).user(argThat((String prompt) -> prompt.contains(name)));
        }

        @Test
        @DisplayName("构造的 Prompt 应包含用户传入的 topic")
        void shouldIncludeTopicInPrompt_whenGenerateJoke() throws Exception {
            // given
            String name = "李四";
            String topic = "健身";

            mockCallResponse("笑话内容");

            // when
            jokeService.generateJoke(name, topic);

            // then
            verify(requestSpec).user(argThat((String prompt) -> prompt.contains(topic)));
        }

        @Test
        @DisplayName("模板参数应正确替换占位符")
        void shouldReplacePlaceholders_whenGenerateJoke() throws Exception {
            // given
            String name = "王五";
            String topic = "旅游";

            mockCallResponse("笑话内容");

            // when
            jokeService.generateJoke(name, topic);

            // then
            verify(requestSpec).user(argThat((String prompt) ->
                    !prompt.contains("{name}") && !prompt.contains("{topic}")));
        }
    }

    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("模板文件不存在时应抛出 TemplateResourceException")
        void shouldThrowException_whenTemplateNotExists() throws Exception {
            // given
            when(jokeTemplateResource.exists()).thenReturn(false);

            // 重新创建服务实例
            jokeService = new JokeServiceImpl(chatClient, aiProperties, jokeTemplateResource);

            // when & then
            TemplateResourceException exception = assertThrows(
                    TemplateResourceException.class,
                    () -> jokeService.generateJoke("小明", "编程")
            );
            assertTrue(exception.getMessage().contains("模板文件不存在"));
        }

        @Test
        @DisplayName("读取模板文件失败时应抛出 TemplateResourceException")
        void shouldThrowException_whenTemplateReadFailed() throws Exception {
            // given
            when(jokeTemplateResource.exists()).thenReturn(true);
            when(jokeTemplateResource.getContentAsString(StandardCharsets.UTF_8))
                    .thenThrow(new RuntimeException("IO异常"));

            // 重新创建服务实例
            jokeService = new JokeServiceImpl(chatClient, aiProperties, jokeTemplateResource);

            // when & then
            TemplateResourceException exception = assertThrows(
                    TemplateResourceException.class,
                    () -> jokeService.generateJoke("小明", "编程")
            );
            assertTrue(exception.getMessage().contains("读取模板文件失败"));
        }
    }

}
