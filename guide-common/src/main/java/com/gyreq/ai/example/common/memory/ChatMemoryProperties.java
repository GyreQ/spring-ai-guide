package com.gyreq.ai.example.common.memory;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 聊天记忆配置属性
 *
 * @author gyreq
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.ai.chat.memory")
public class ChatMemoryProperties {

    /**
     * 存储类型：memory（默认）、redis、jdbc
     */
    private String type = "memory";

    /**
     * 最大历史消息条数
     */
    private int maxMessages = 20;

    /**
     * Redis Key 前缀
     */
    private String redisKeyPrefix = "chat:memory:";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
    }

}
