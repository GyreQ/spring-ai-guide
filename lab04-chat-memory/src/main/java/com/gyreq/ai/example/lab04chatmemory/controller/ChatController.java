package com.gyreq.ai.example.lab04chatmemory.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab04chatmemory.dto.ChatRequestDTO;
import com.gyreq.ai.example.lab04chatmemory.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab04chatmemory.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天控制器
 *
 * <p>提供带记忆功能的聊天 REST API 接口。
 *
 * @author gyreq
 * @since 1.0.0
 */
@RestController
@RequestMapping("/lab04")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 带记忆的聊天接口
     *
     * <p>根据 sessionId 维护对话上下文，实现多轮对话。
     *
     * @param request 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public Result<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = chatService.chat(request.sessionId(), request.content());
        return Result.success(response);
    }

}
