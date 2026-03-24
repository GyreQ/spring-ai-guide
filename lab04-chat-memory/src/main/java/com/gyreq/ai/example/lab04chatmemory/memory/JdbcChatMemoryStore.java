package com.gyreq.ai.example.lab04chatmemory.memory;

import com.gyreq.ai.example.common.memory.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JDBC 聊天记忆存储
 *
 * <p>使用 {@link JdbcTemplate} 实现，适用于企业级持久化场景。
 *
 * <p>条件装配：当配置 spring.ai.chat.memory.type=jdbc 时生效。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(
        name = "spring.ai.chat.memory.type",
        havingValue = "jdbc"
)
public class JdbcChatMemoryStore implements ChatMemory, ChatMemoryStore {

    private static final String TYPE_USER = "user";
    private static final String TYPE_ASSISTANT = "assistant";
    private static final String TYPE_SYSTEM = "system";

    private static final String SQL_INSERT =
            "INSERT INTO chat_memory (conversation_id, message_type, content) VALUES (?, ?, ?)";

    private static final String SQL_SELECT =
            "SELECT message_type, content FROM chat_memory " +
            "WHERE conversation_id = ? ORDER BY create_time ASC";

    private static final String SQL_DELETE =
            "DELETE FROM chat_memory WHERE conversation_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public JdbcChatMemoryStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(String conversationId, List<Message> messages) {
        log.debug("追加消息到数据库会话 {}: {} 条消息", conversationId, messages.size());

        for (Message message : messages) {
            String type = getMessageType(message);
            jdbcTemplate.update(SQL_INSERT, conversationId, type, message.getText());
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        return jdbcTemplate.query(SQL_SELECT,
                (rs, rowNum) -> buildMessage(rs.getString("message_type"), rs.getString("content")),
                conversationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(String conversationId) {
        log.debug("清除数据库会话 {} 的记忆", conversationId);
        jdbcTemplate.update(SQL_DELETE, conversationId);
    }

    private String getMessageType(Message message) {
        if (message instanceof UserMessage) {
            return TYPE_USER;
        } else if (message instanceof AssistantMessage) {
            return TYPE_ASSISTANT;
        } else if (message instanceof SystemMessage) {
            return TYPE_SYSTEM;
        }
        return TYPE_USER;
    }

    private Message buildMessage(String type, String content) {
        return switch (type) {
            case TYPE_USER -> new UserMessage(content);
            case TYPE_ASSISTANT -> new AssistantMessage(content);
            case TYPE_SYSTEM -> new SystemMessage(content);
            default -> new UserMessage(content);
        };
    }

}
