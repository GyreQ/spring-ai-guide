package com.gyreq.ai.example.lab02prompttemplates.service;

import com.gyreq.ai.example.lab02prompttemplates.dto.AssistantResponseDTO;

/**
 * 助手服务接口
 *
 * <p>提供角色扮演助手的核心业务方法。
 * 使用 SystemPromptTemplate 设置系统角色/人设。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface AssistantService {

    /**
     * 与角色扮演助手对话
     *
     * @param name     助手名字
     * @param voice    助手风格
     * @param question 用户问题
     * @return 助手响应
     */
    AssistantResponseDTO chat(String name, String voice, String question);
}
