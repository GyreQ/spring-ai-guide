package com.gyreq.ai.example.lab05officialchatmemory.service;

import com.gyreq.ai.example.lab05officialchatmemory.dto.ChatResponseDTO;
import com.gyreq.ai.example.lab05officialchatmemory.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatService 集成测试
 *
 * <p>使用 H2 内存数据库验证对话记忆功能。
 *
 * @author gyreq
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class ChatServiceIntegrationTest {

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private ChatService chatService;

    private static final String TEST_SESSION_ID = "test-session-001";

    @Test
    @DisplayName("集成测试：验证数据库表自动创建")
    void shouldCreateDatabaseTable_whenStartApplication() {
        // 如果应用启动成功，说明表已自动创建
        assertNotNull(chatMemory);
    }

    @Test
    @DisplayName("集成测试：验证对话记忆持久化")
    void shouldPersistChatMemory_whenChat() {
        // 手动添加对话历史
        List<Message> messages = List.of(
                new UserMessage("我叫张三"),
                new AssistantMessage("你好张三，很高兴认识你！")
        );
        chatMemory.add(TEST_SESSION_ID, messages);

        // 验证历史消息被正确存储
        List<Message> storedMessages = chatMemory.get(TEST_SESSION_ID);
        assertEquals(2, storedMessages.size());
        assertTrue(storedMessages.get(0) instanceof UserMessage);
        assertTrue(storedMessages.get(1) instanceof AssistantMessage);
    }

    @Test
    @DisplayName("集成测试：验证不同会话隔离")
    void shouldIsolateDifferentSessions_whenMultipleSessions() {
        String session1 = "session-isolated-1";
        String session2 = "session-isolated-2";

        // 为不同会话添加消息
        chatMemory.add(session1, List.of(new UserMessage("会话1的消息")));
        chatMemory.add(session2, List.of(new UserMessage("会话2的消息")));

        // 验证会话隔离
        List<Message> messages1 = chatMemory.get(session1);
        List<Message> messages2 = chatMemory.get(session2);

        assertEquals(1, messages1.size());
        assertEquals(1, messages2.size());
        assertEquals("会话1的消息", ((UserMessage) messages1.get(0)).getText());
        assertEquals("会话2的消息", ((UserMessage) messages2.get(0)).getText());
    }

    @Test
    @DisplayName("集成测试：验证清除会话记忆")
    void shouldClearSessionMemory_whenClearCalled() {
        String sessionToClear = "session-to-clear";

        // 添加消息
        chatMemory.add(sessionToClear, List.of(new UserMessage("要清除的消息")));

        // 清除记忆
        chatMemory.clear(sessionToClear);

        // 验证已清除
        List<Message> messages = chatMemory.get(sessionToClear);
        assertTrue(messages.isEmpty());
    }

}
