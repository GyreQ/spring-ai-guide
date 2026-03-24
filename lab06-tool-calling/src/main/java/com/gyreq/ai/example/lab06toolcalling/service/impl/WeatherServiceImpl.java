package com.gyreq.ai.example.lab06toolcalling.service.impl;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab06toolcalling.dto.WeatherResponseDTO;
import com.gyreq.ai.example.lab06toolcalling.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * 天气服务实现类
 *
 * <p>使用配置了默认工具的 ChatClient 进行对话。
 * Service 层无需关心工具调用细节，框架会自动处理：
 * <ol>
 *   <li>模型分析用户问题，判断是否需要调用工具</li>
 *   <li>如果需要，模型返回工具调用请求（Tool Call Request）</li>
 *   <li>框架执行对应的工具方法，获取结果</li>
 *   <li>将工具结果返回给模型，模型生成最终回复</li>
 * </ol>
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final ChatClient chatClient;
    private final AiProperties aiProperties;

    @Override
    public WeatherResponseDTO chat(String question) {
        log.info("用户问题: {}", question);

        // 调用 ChatClient，工具会自动被触发
        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();

        log.info("AI 回复: {}", answer);
        log.info("使用模型: {}", aiProperties.getModel());

        return WeatherResponseDTO.of(question, answer);
    }

}
