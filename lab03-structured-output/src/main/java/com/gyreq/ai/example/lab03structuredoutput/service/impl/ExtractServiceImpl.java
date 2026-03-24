package com.gyreq.ai.example.lab03structuredoutput.service.impl;

import com.gyreq.ai.example.lab03structuredoutput.dto.ExtractResponseDTO;
import com.gyreq.ai.example.lab03structuredoutput.exception.StructuredOutputParseException;
import com.gyreq.ai.example.lab03structuredoutput.model.PersonInfo;
import com.gyreq.ai.example.lab03structuredoutput.service.ExtractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * 信息提取服务实现类
 *
 * <p>使用 Spring AI 的 ChatClient.entity() 方法实现结构化输出。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractServiceImpl implements ExtractService {

    private final ChatClient chatClient;

    @Override
    public ExtractResponseDTO extractPersonInfo(String text) {
        log.info("开始提取人员信息: {}", text);

        // 构建提示词
        String prompt = buildPrompt(text);
        log.debug("构造的 Prompt: {}", prompt);

        try {
            // 调用大模型，直接获取结构化对象
            // Spring AI 会自动：
            // 1. 根据目标类型生成 JSON Schema
            // 2. 将 Schema 注入到系统提示中
            // 3. 解析模型响应并映射为 POJO
            PersonInfo personInfo = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(PersonInfo.class);

            // 检查是否成功解析
            if (personInfo == null) {
                throw new StructuredOutputParseException(
                        "无法从文本中提取有效的结构化信息，模型返回空结果");
            }

            log.info("提取成功: {}", personInfo);
            return ExtractResponseDTO.of(personInfo, text);

        } catch (StructuredOutputParseException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("提取人员信息失败", e);
            throw new StructuredOutputParseException(
                    "无法从文本中提取有效的结构化信息，请检查输入内容", e);
        }
    }

    /**
     * 构建提示词
     *
     * @param text 待提取的文本
     * @return 完整的提示词
     */
    private String buildPrompt(String text) {
        return """
                请从以下文本中提取人员信息，包括：
                - name: 姓名
                - age: 年龄（整数）
                - hobbies: 爱好列表（字符串数组）

                文本：%s

                请以 JSON 格式返回结果。
                """.formatted(text);
    }

}
