package com.gyreq.ai.example.lab07ragmilvus.service;

import com.gyreq.ai.example.common.config.AiProperties;
import com.gyreq.ai.example.common.test.BaseChatClientTest;
import com.gyreq.ai.example.lab07ragmilvus.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab07ragmilvus.dto.UploadResponseDTO;
import com.gyreq.ai.example.lab07ragmilvus.exception.DocumentProcessException;
import com.gyreq.ai.example.lab07ragmilvus.service.impl.KnowledgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * KnowledgeService 单元测试
 *
 * @author gyreq
 * @since 1.0.0
 */
class KnowledgeServiceTest extends BaseChatClientTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private AiProperties aiProperties;

    private KnowledgeService knowledgeService;

    @BeforeEach
    void setUp() {
        lenient().when(aiProperties.getModel()).thenReturn("test-model");
        knowledgeService = new KnowledgeServiceImpl(vectorStore, chatClient, aiProperties);
    }

    @Test
    @DisplayName("上传文档时应返回入库成功的文本片段数量")
    void shouldReturnChunkCount_whenUploadDocument() {
        // Given
        String content = "This is a test document content for testing the upload functionality.";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes()
        );

        // VectorStore.add 不抛出异常即表示成功
        doNothing().when(vectorStore).add(anyList());

        // When
        UploadResponseDTO response = knowledgeService.upload(file);

        // Then
        assertNotNull(response);
        assertTrue(response.chunkCount() > 0, "应该有至少一个文本片段");
        assertEquals("test.txt", response.fileName());
        verify(vectorStore).add(anyList());
    }

    @Test
    @DisplayName("上传空文件时应抛出 DocumentProcessException")
    void shouldThrowDocumentProcessException_whenUploadEmptyFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        // When & Then
        assertThrows(DocumentProcessException.class, () -> knowledgeService.upload(file));
    }

    @Test
    @DisplayName("聊天时应返回 AI 响应")
    void shouldReturnAiResponse_whenChat() {
        // Given
        String question = "什么是 Spring AI?";
        String expectedResponse = "Spring AI 是一个用于构建 AI 应用的 Spring 框架。";
        mockCallResponse(expectedResponse);

        // When
        ChatResponseDTO response = knowledgeService.chat(question);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response.content());
        assertEquals("test-model", response.model());
    }

    @Test
    @DisplayName("聊天时应该调用 ChatClient")
    void shouldCallChatClient_whenChat() {
        // Given
        String question = "测试问题";
        mockCallResponse("测试回答");

        // When
        knowledgeService.chat(question);

        // Then
        verify(chatClient).prompt();
    }

    @Test
    @DisplayName("上传 IO 异常时应抛出 DocumentProcessException")
    void shouldThrowDocumentProcessException_whenIoException() throws IOException {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("IO Error"));

        // When & Then
        assertThrows(DocumentProcessException.class, () -> knowledgeService.upload(file));
    }

}
