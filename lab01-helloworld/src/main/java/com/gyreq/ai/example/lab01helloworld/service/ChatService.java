package com.gyreq.ai.example.lab01helloworld.service;

import com.gyreq.ai.example.lab01helloworld.dto.ChatResponseDTO;
import reactor.core.publisher.Flux;

/**
 * 聊天服务接口
 *
 * <p>提供与大模型交互的核心业务方法。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface ChatService {

    /**
     * 同步聊天：发送消息并等待完整响应
     *
     * @param message 用户消息
     * @return AI 回复
     */
    ChatResponseDTO chat(String message);

    /**
     * 流式聊天：发送消息并以流的形式接收响应
     *
     * <p>适用于长文本生成场景，用户可以实时看到生成过程。
     *
     * @param message 用户消息
     * @return AI 回复流
     */
    Flux<String> chatStream(String message);

}
