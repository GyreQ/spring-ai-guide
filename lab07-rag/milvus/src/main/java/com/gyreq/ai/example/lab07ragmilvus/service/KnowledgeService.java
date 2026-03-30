package com.gyreq.ai.example.lab07ragmilvus.service;

import com.gyreq.ai.example.lab07ragmilvus.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab07ragmilvus.dto.UploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库服务接口
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface KnowledgeService {

    /**
     * 上传文档到知识库
     *
     * @param file 上传的文件
     * @return 入库结果
     */
    UploadResponseDTO upload(MultipartFile file);

    /**
     * 基于知识库回答问题
     *
     * @param question 用户问题
     * @return AI 回答
     */
    ChatResponseDTO chat(String question);

}
