package com.gyreq.ai.example.lab07ragsimple.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab07ragsimple.dto.ChatRequestDTO;
import com.gyreq.ai.example.lab07ragsimple.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab07ragsimple.dto.UploadResponseDTO;
import com.gyreq.ai.example.lab07ragsimple.service.KnowledgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库控制器
 *
 * <p>提供知识库入库和问答接口。
 *
 * @author gyreq
 * @since 1.0.0
 */
@RestController
@RequestMapping("/lab07/simple")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * 上传文档到知识库
     *
     * <p>接收 MultipartFile，解析并存入向量库。
     *
     * @param file 上传的文件
     * @return 入库结果
     */
    @PostMapping("/upload")
    public Result<UploadResponseDTO> upload(@RequestParam("file") MultipartFile file) {
        UploadResponseDTO response = knowledgeService.upload(file);
        return Result.success(response);
    }

    /**
     * 基于知识库回答问题
     *
     * <p>使用 RAG 模式，基于向量库中的知识回答问题。
     *
     * @param request 聊天请求
     * @return AI 回答
     */
    @PostMapping("/chat")
    public Result<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = knowledgeService.chat(request.question());
        return Result.success(response);
    }

}
