package com.gyreq.ai.example.lab01helloworld.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab01helloworld.dto.ChatRequestDTO;
import com.gyreq.ai.example.lab01helloworld.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab01helloworld.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 聊天控制器
 *
 * <p>提供 AI 聊天相关的 REST API 接口。
 *
 * @author gyreq
 * @since 1.0.0
 */
@RestController
@RequestMapping("/lab01")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 同步聊天接口
     *
     * <p>发送消息并等待 AI 完整回复。
     *
     * @param request 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public Result<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = chatService.chat(request.message());
        return Result.success(response);
    }

    /**
     * 流式聊天接口
     *
     * <p>发送消息并以 SSE 方式返回 AI 回复，适用于长文本生成场景。
     *
     * @param request 聊天请求
     * @return AI 回复流
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody ChatRequestDTO request) {
        return chatService.chatStream(request.message());
    }

}
