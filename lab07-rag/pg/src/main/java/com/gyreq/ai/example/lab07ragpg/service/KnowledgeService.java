package com.gyreq.ai.example.lab07ragpg.service;

import com.gyreq.ai.example.lab07ragpg.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab07ragpg.dto.UploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库服务接口
 *
 * <p>提供知识库入库和问答功能。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface KnowledgeService {

    /**
     * 上传文档到知识库
     *
     * <p>解析文档、分割文本、存入向量库。
     *
     * @param file 上传的文件
     * @return 入库结果
     */
    UploadResponseDTO upload(MultipartFile file);

    /**
     * 基于知识库回答问题
     *
     * <p>使用 RAG（检索增强生成）模式，先在向量库中检索相关文档，
     * 再基于检索结果生成回答。
     *
     * @param question 用户问题
     * @return AI 回答
     */
    ChatResponseDTO chat(String question);

}
