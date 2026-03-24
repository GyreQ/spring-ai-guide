package com.gyreq.ai.example.lab04chatmemory.service;

import com.gyreq.ai.example.lab04chatmemory.dto.ChatResponseDTO;

/**
 * 聊天服务接口
 *
 * <p>提供带记忆功能的聊天服务。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface ChatService {

    /**
     * 带记忆的聊天
     *
     * @param sessionId 会话 ID
     * @param content   用户消息
     * @return AI 回复
     */
    ChatResponseDTO chat(String sessionId, String content);

}
