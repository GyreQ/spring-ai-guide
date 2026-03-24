package com.gyreq.ai.example.lab06toolcalling.config;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab06toolcalling.tools.WeatherTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient 配置类
 *
 * <p>配置带有默认工具的 ChatClient Bean，覆盖 guide-common 的默认配置。
 *
 * <p>根据 Spring AI 官方文档，使用 {@code ChatClient.builder().defaultTools()}
 * 注册默认工具，框架会在需要时自动调用工具方法。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
@RequiredArgsConstructor
public class ToolCallingChatClientConfig {

    private final WeatherTools weatherTools;

    /**
     * 创建带有默认工具的 ChatClient Bean
     *
     * <p>通过 {@code defaultTools()} 方法注册工具类实例，
     * Spring AI 会自动扫描 {@code @Tool} 注解的方法并生成对应的 ToolCallback。
     *
     * <p>当用户提问涉及到天气查询时，模型会自动判断是否需要调用工具，
     * 并在调用后根据工具返回结果生成最终回复。
     *
     * @param builder ChatClient 构建器
     * @return 配置了默认工具的 ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultTools(weatherTools)
                .build();
    }

}
