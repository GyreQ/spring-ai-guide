package com.gyreq.ai.example.lab02prompttemplates.service.impl;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab02prompttemplates.dto.AssistantResponseDTO;
import com.gyreq.ai.example.lab02prompttemplates.exception.TemplateResourceException;
import com.gyreq.ai.example.lab02prompttemplates.service.AssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 助手服务实现类
 *
 * <p>使用 {@link SystemPromptTemplate} 加载外部系统角色模板，
 * 结合 {@link UserMessage} 构建多消息 {@link Prompt} 发送给模型。
 *
 * <p>参考 Spring AI 官方文档：
 * <a href="https://docs.spring.io/spring-ai/reference/api/prompt.html">Prompt API</a>
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
public class AssistantServiceImpl implements AssistantService {

    private final ChatModel chatModel;
    private final AiProperties aiProperties;
    private final Resource systemAssistantTemplateResource;

    /**
     * 构造函数注入
     *
     * @param chatModel                      ChatModel 实例（用于构建 Prompt）
     * @param aiProperties                   AI 配置属性
     * @param systemAssistantTemplateResource 系统助手模板资源
     */
    public AssistantServiceImpl(
            ChatModel chatModel,
            AiProperties aiProperties,
            @Value("classpath:/prompts/system-assistant.st") Resource systemAssistantTemplateResource) {
        this.chatModel = chatModel;
        this.aiProperties = aiProperties;
        this.systemAssistantTemplateResource = systemAssistantTemplateResource;
    }

    @Override
    public AssistantResponseDTO chat(String name, String voice, String question) {
        log.info("角色扮演对话请求: name={}, voice={}, question={}", name, voice, question);

        // 1. 加载系统模板文件
        String systemTemplateContent = loadTemplateContent();

        // 2. 使用 SystemPromptTemplate 创建系统消息
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemTemplateContent);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of(
                "name", name,
                "voice", voice
        ));

        log.debug("系统消息: {}", systemMessage.getText());

        // 3. 创建用户消息
        UserMessage userMessage = new UserMessage(question);

        // 4. 组合成 Prompt 发送给模型
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        log.info("AI 助手回复: {}", response);

        return AssistantResponseDTO.of(response, name, voice, question);
    }

    /**
     * 加载模板文件内容
     *
     * @return 模板内容字符串
     * @throws TemplateResourceException 当模板文件缺失或读取失败时抛出
     */
    private String loadTemplateContent() {
        try {
            if (!systemAssistantTemplateResource.exists()) {
                throw new TemplateResourceException("模板文件不存在: prompts/system-assistant.st");
            }
            return systemAssistantTemplateResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取模板文件失败", e);
            throw new TemplateResourceException("读取模板文件失败: " + e.getMessage(), e);
        }
    }
}
