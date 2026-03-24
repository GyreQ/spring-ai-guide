package com.gyreq.ai.example.lab03structuredoutput.service;

import com.gyreq.ai.example.common.test.BaseChatClientTest;
import com.gyreq.ai.example.lab03structuredoutput.dto.ExtractResponseDTO;
import com.gyreq.ai.example.lab03structuredoutput.exception.StructuredOutputParseException;
import com.gyreq.ai.example.lab03structuredoutput.model.PersonInfo;
import com.gyreq.ai.example.lab03structuredoutput.service.impl.ExtractServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ExtractService 单元测试
 *
 * <p>继承 BaseChatClientTest，复用 ChatClient 的 Mock 配置。
 *
 * @author gyreq
 * @since 1.0.0
 */
class ExtractServiceTest extends BaseChatClientTest {

    @Mock
    private ChatClient.CallResponseSpec callResponseSpecWithEntity;

    private ExtractService extractService;

    @BeforeEach
    void setUp() {
        extractService = new ExtractServiceImpl(chatClient);
    }

    @Nested
    @DisplayName("正常提取测试")
    class NormalExtractionTest {

        @Test
        @DisplayName("应正确解析模型返回的结构化对象")
        void shouldParseEntity_whenModelReturnsValidData() {
            // given
            String inputText = "张三，28岁，爱好是编程和打篮球";
            PersonInfo expectedPerson = new PersonInfo("张三", 28, List.of("编程", "打篮球"));

            // 配置 Mock 链
            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.entity(PersonInfo.class)).thenReturn(expectedPerson);

            // when
            ExtractResponseDTO response = extractService.extractPersonInfo(inputText);

            // then
            assertNotNull(response);
            assertNotNull(response.personInfo());
            assertEquals("张三", response.personInfo().getName());
            assertEquals(28, response.personInfo().getAge());
            assertEquals(List.of("编程", "打篮球"), response.personInfo().getHobbies());
            assertEquals(inputText, response.originalText());
            assertTrue(response.timestamp() > 0);
        }

        @Test
        @DisplayName("应正确解析包含单个爱好的人员信息")
        void shouldParseEntity_whenSingleHobby() {
            // given
            String inputText = "李四，30岁，爱好是读书";
            PersonInfo expectedPerson = new PersonInfo("李四", 30, List.of("读书"));

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.entity(PersonInfo.class)).thenReturn(expectedPerson);

            // when
            ExtractResponseDTO response = extractService.extractPersonInfo(inputText);

            // then
            assertNotNull(response.personInfo());
            assertEquals("李四", response.personInfo().getName());
            assertEquals(30, response.personInfo().getAge());
            assertEquals(1, response.personInfo().getHobbies().size());
        }

        @Test
        @DisplayName("Prompt 应包含输入文本")
        void shouldIncludeInputTextInPrompt() {
            // given
            String inputText = "测试文本";
            PersonInfo expectedPerson = new PersonInfo("测试", 0, List.of());

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.entity(PersonInfo.class)).thenReturn(expectedPerson);

            // when
            extractService.extractPersonInfo(inputText);

            // then
            verify(requestSpec).user(argThat((String prompt) -> prompt.contains(inputText)));
        }
    }

    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("模型无法解析时应抛出 StructuredOutputParseException")
        void shouldThrowException_whenModelCannotParse() {
            // given
            String inputText = "这是一段无法提取信息的文本";

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.entity(PersonInfo.class))
                    .thenThrow(new RuntimeException("Failed to parse"));

            // when & then
            StructuredOutputParseException exception = assertThrows(
                    StructuredOutputParseException.class,
                    () -> extractService.extractPersonInfo(inputText)
            );

            assertTrue(exception.getMessage().contains("无法从文本中提取有效的结构化信息"));
        }

        @Test
        @DisplayName("模型返回 null 时应抛出 StructuredOutputParseException")
        void shouldThrowException_whenModelReturnsNull() {
            // given
            String inputText = "张三";

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.entity(PersonInfo.class)).thenReturn(null);

            // when & then
            // 由于返回 null，后续处理可能会 NPE，被 catch 并包装为 StructuredOutputParseException
            assertThrows(
                    StructuredOutputParseException.class,
                    () -> extractService.extractPersonInfo(inputText)
            );
        }
    }

}
