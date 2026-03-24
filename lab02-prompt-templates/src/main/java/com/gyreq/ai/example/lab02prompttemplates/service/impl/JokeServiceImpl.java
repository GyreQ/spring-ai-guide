package com.gyreq.ai.example.lab02prompttemplates.service.impl;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab02prompttemplates.dto.JokeResponseDTO;
import com.gyreq.ai.example.lab02prompttemplates.exception.TemplateResourceException;
import com.gyreq.ai.example.lab02prompttemplates.service.JokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 笑话服务实现类
 *
 * <p>使用 {@link PromptTemplate} 加载外部模板文件，实现提示词与代码解耦。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
public class JokeServiceImpl implements JokeService {

    private final ChatClient chatClient;
    private final AiProperties aiProperties;
    private final Resource jokeTemplateResource;

    /**
     * 构造函数注入
     *
     * @param chatClient         ChatClient 实例
     * @param aiProperties       AI 配置属性
     * @param jokeTemplateResource 笑话模板资源
     */
    public JokeServiceImpl(
            ChatClient chatClient,
            AiProperties aiProperties,
            @Value("classpath:/prompts/joke.st") Resource jokeTemplateResource) {
        this.chatClient = chatClient;
        this.aiProperties = aiProperties;
        this.jokeTemplateResource = jokeTemplateResource;
    }

    @Override
    public JokeResponseDTO generateJoke(String name, String topic) {
        log.info("生成笑话请求: name={}, topic={}", name, topic);

        // 1. 加载模板文件
        String templateContent = loadTemplateContent();

        // 2. 创建 PromptTemplate 并填充参数
        PromptTemplate promptTemplate = new PromptTemplate(templateContent);
        String prompt = promptTemplate.render(Map.of(
                "name", name,
                "topic", topic
        ));

        log.debug("构造的 Prompt: {}", prompt);

        // 3. 调用 AI 模型
        String joke = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.info("AI 生成的笑话: {}", joke);

        return JokeResponseDTO.of(joke, name, topic);
    }

    /**
     * 加载模板文件内容
     *
     * @return 模板内容字符串
     * @throws TemplateResourceException 当模板文件缺失或读取失败时抛出
     */
    private String loadTemplateContent() {
        try {
            if (!jokeTemplateResource.exists()) {
                throw new TemplateResourceException("模板文件不存在: prompts/joke.st");
            }
            return jokeTemplateResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取模板文件失败", e);
            throw new TemplateResourceException("读取模板文件失败: " + e.getMessage(), e);
        }
    }

}
