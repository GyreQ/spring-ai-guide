package com.gyreq.ai.example.lab06toolcalling.service;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.common.test.BaseChatClientTest;
import com.gyreq.ai.example.lab06toolcalling.dto.WeatherResponseDTO;
import com.gyreq.ai.example.lab06toolcalling.service.impl.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * WeatherService 单元测试
 *
 * <p>测试工具调用场景，验证 ChatClient 能正确处理用户问题。
 *
 * @author gyreq
 * @since 1.0.0
 */
class WeatherServiceTest extends BaseChatClientTest {

    @Mock
    private AiProperties aiProperties;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        when(aiProperties.getModel()).thenReturn("test-model");
        weatherService = new WeatherServiceImpl(chatClient, aiProperties);
    }

    @Test
    @DisplayName("发送天气查询问题时应返回 AI 回复")
    void shouldReturnResponse_whenAskAboutWeather() {
        // Given: 模拟 AI 回复包含天气信息
        String question = "北京今天天气怎么样？";
        String expectedAnswer = "根据查询结果，北京今天天气晴朗，温度28℃，湿度45%。";
        mockCallResponse(expectedAnswer);

        // When: 调用服务
        WeatherResponseDTO response = weatherService.chat(question);

        // Then: 验证响应
        assertNotNull(response);
        assertEquals(question, response.question());
        assertEquals(expectedAnswer, response.answer());
    }

    @Test
    @DisplayName("发送天气预报问题时应返回预报信息")
    void shouldReturnForecast_whenAskAboutForecast() {
        // Given: 模拟 AI 回复包含天气预报
        String question = "上海未来3天天气预报";
        String expectedAnswer = "上海未来3天天气预报：第1天28℃多云，第2天30℃晴，第3天27℃小雨。";
        mockCallResponse(expectedAnswer);

        // When: 调用服务
        WeatherResponseDTO response = weatherService.chat(question);

        // Then: 验证响应
        assertNotNull(response);
        assertEquals(question, response.question());
        assertEquals(expectedAnswer, response.answer());
    }

    @Test
    @DisplayName("查询不存在的城市天气时应返回错误提示")
    void shouldReturnError_whenCityNotFound() {
        // Given: 模拟 AI 对无法查询的城市的回复
        String question = "火星的天气怎么样？";
        String expectedAnswer = "抱歉，我无法查询火星的天气信息，请提供一个地球上的城市名称。";
        mockCallResponse(expectedAnswer);

        // When: 调用服务
        WeatherResponseDTO response = weatherService.chat(question);

        // Then: 验证响应
        assertNotNull(response);
        assertEquals(question, response.question());
        assertEquals(expectedAnswer, response.answer());
    }

    @Test
    @DisplayName("发送非天气相关问题时应返回普通回复")
    void shouldReturnNormalResponse_whenAskNonWeatherQuestion() {
        // Given: 模拟 AI 普通回复
        String question = "你好，介绍一下自己";
        String expectedAnswer = "你好！我是一个 AI 助手，可以帮助你查询天气信息。";
        mockCallResponse(expectedAnswer);

        // When: 调用服务
        WeatherResponseDTO response = weatherService.chat(question);

        // Then: 验证响应
        assertNotNull(response);
        assertEquals(question, response.question());
        assertEquals(expectedAnswer, response.answer());
    }

}
