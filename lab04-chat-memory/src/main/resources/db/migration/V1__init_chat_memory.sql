-- 聊天记忆表
CREATE TABLE IF NOT EXISTS chat_memory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话 ID',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型：user/assistant/system',
    content TEXT NOT NULL COMMENT '消息内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_conversation_time (conversation_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天记忆表';
