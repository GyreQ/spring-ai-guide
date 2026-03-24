package com.gyreq.ai.example.lab05officialchatmemory.service;

import com.gyreq.ai.example.lab05officialchatmemory.dto.ChatResponseDTO;

/**
 * 聊天服务接口
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface ChatService {

    /**
     * 带记忆的聊天
     *
     * <p>根据 sessionId 维护对话上下文，实现多轮对话。
     *
     * @param sessionId 会话 ID
     * @param content 用户消息
     * @return AI 回复
     */
    ChatResponseDTO chat(String sessionId, String content);

}
