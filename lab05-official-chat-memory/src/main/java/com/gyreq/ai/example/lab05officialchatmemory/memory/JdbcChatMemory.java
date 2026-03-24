package com.gyreq.ai.example.lab05officialchatmemory.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * JDBC 对话记忆实现
 *
 * <p>使用数据库持久化对话记忆，支持：
 * <ul>
 *     <li>自动建表（应用启动时）</li>
 *     <li>滑动窗口限制（最近 N 条消息）</li>
 *     <li>会话隔离</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Component
public class JdbcChatMemory implements ChatMemory {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS chat_memory (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                conversation_id VARCHAR(255) NOT NULL,
                message_type VARCHAR(20) NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_conversation_id (conversation_id)
            )
            """;

    private static final String INSERT_SQL = """
            INSERT INTO chat_memory (conversation_id, message_type, content)
            VALUES (?, ?, ?)
            """;

    private static final String SELECT_SQL = """
            SELECT message_type, content FROM chat_memory
            WHERE conversation_id = ?
            ORDER BY created_at ASC
            """;

    private static final String DELETE_SQL = """
            DELETE FROM chat_memory WHERE conversation_id = ?
            """;

    private static final String COUNT_SQL = """
            SELECT COUNT(*) FROM chat_memory WHERE conversation_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final int maxMessages;

    /**
     * 构造函数
     *
     * @param jdbcTemplate JDBC 模板
     */
    public JdbcChatMemory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.maxMessages = 20;
        initTable();
    }

    /**
     * 初始化数据库表
     */
    private void initTable() {
        try {
            jdbcTemplate.execute(CREATE_TABLE_SQL);
            log.info("聊天记忆表初始化成功");
        } catch (Exception e) {
            log.warn("聊天记忆表初始化失败（可能已存在）: {}", e.getMessage());
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        log.debug("添加 {} 条消息到会话 {}", messages.size(), conversationId);

        for (Message message : messages) {
            String messageType = message.getMessageType().name();
            String content = message.getText();
            jdbcTemplate.update(INSERT_SQL, conversationId, messageType, content);
        }

        // 滑动窗口：删除超出限制的旧消息
        trimMessages(conversationId);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> messages = new ArrayList<>();

        jdbcTemplate.query(SELECT_SQL, rs -> {
            String messageType = rs.getString("message_type");
            String content = rs.getString("content");

            Message message = switch (messageType) {
                case "USER" -> new UserMessage(content);
                case "ASSISTANT" -> new AssistantMessage(content);
                default -> null;
            };

            if (message != null) {
                messages.add(message);
            }
        }, conversationId);

        log.debug("从会话 {} 加载 {} 条消息", conversationId, messages.size());
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        log.debug("清除会话 {} 的记忆", conversationId);
        jdbcTemplate.update(DELETE_SQL, conversationId);
    }

    /**
     * 裁剪消息，保持滑动窗口
     *
     * @param conversationId 会话 ID
     */
    private void trimMessages(String conversationId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class, conversationId);
        if (count != null && count > maxMessages) {
            // 删除最旧的消息
            String deleteOldSql = """
                    DELETE FROM chat_memory
                    WHERE conversation_id = ?
                    AND id NOT IN (
                        SELECT id FROM (
                            SELECT id FROM chat_memory
                            WHERE conversation_id = ?
                            ORDER BY created_at DESC
                            LIMIT ?
                        ) AS recent
                    )
                    """;
            jdbcTemplate.update(deleteOldSql, conversationId, conversationId, maxMessages);
            log.debug("裁剪会话 {} 的消息，保留最近 {} 条", conversationId, maxMessages);
        }
    }

}
