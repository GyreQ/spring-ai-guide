package com.gyreq.ai.example.lab07ragpg.service.impl;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.lab07ragpg.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab07ragpg.dto.UploadResponseDTO;
import com.gyreq.ai.example.lab07ragpg.exception.DocumentProcessException;
import com.gyreq.ai.example.lab07ragpg.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库服务实现类
 *
 * <p>实现文档入库和 RAG 问答功能。
 * 业务逻辑与 simple 模块完全相同，仅向量库实现不同。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final AiProperties aiProperties;

    /**
     * 上传文档到知识库
     *
     * <p>ETL 流程：
     * <ol>
     *   <li>Extract: 使用 TikaDocumentReader 读取文件</li>
     *   <li>Transform: 使用 TokenTextSplitter 分割文本</li>
     *   <li>Load: 存入 VectorStore</li>
     * </ol>
     *
     * @param file 上传的文件
     * @return 入库结果
     */
    @Override
    public UploadResponseDTO upload(MultipartFile file) {
        // 检查空文件
        if (file.isEmpty()) {
            throw new DocumentProcessException("文件内容为空");
        }

        try {
            // 1. Extract - 使用 TikaDocumentReader 读取文档
            TikaDocumentReader reader = new TikaDocumentReader(
                    new InputStreamResource(file.getInputStream())
            );
            List<Document> documents = reader.get();

            // 2. Transform - 使用 TokenTextSplitter 分割文本
            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(800)
                    .withMinChunkSizeChars(350)
                    .withMinChunkLengthToEmbed(5)
                    .withKeepSeparator(true)
                    .build();
            List<Document> chunks = splitter.apply(documents);

            // 3. Load - 存入向量库
            vectorStore.add(chunks);

            log.info("文档入库成功: fileName={}, chunkCount={}", file.getOriginalFilename(), chunks.size());

            return UploadResponseDTO.of(chunks.size(), file.getOriginalFilename());
        } catch (Exception e) {
            log.error("文档处理失败: fileName={}", file.getOriginalFilename(), e);
            throw new DocumentProcessException("文档处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 基于知识库回答问题
     *
     * <p>使用 QuestionAnswerAdvisor 实现 RAG：
     * <ol>
     *   <li>Advisor 自动在向量库中检索相关文档</li>
     *   <li>将检索结果作为上下文注入到 Prompt</li>
     *   <li>模型基于上下文生成回答</li>
     * </ol>
     *
     * @param question 用户问题
     * @return AI 回答
     */
    @Override
    public ChatResponseDTO chat(String question) {
        String response = chatClient.prompt()
                .user(question)
                .call()
                .content();

        return ChatResponseDTO.of(response, aiProperties);
    }

}
