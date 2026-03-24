package com.gyreq.ai.example.common.memory;

/**
 * 聊天记忆存储标记接口
 *
 * <p>本接口用于类型标识，实际实现类应直接实现 Spring AI 的
 * {@link org.springframework.ai.chat.memory.ChatMemory} 接口。
 *
 * <p>这样设计的原因：
 * <ul>
 *     <li>确保与 Spring AI Advisor 模式完全兼容</li>
 *     <li>避免方法签名不匹配的问题</li>
 *     <li>允许实现类使用 Spring AI 内置的 InMemoryChatMemory 作为委托</li>
 * </ul>
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface ChatMemoryStore {

    // 标记接口，不定义方法
    // 实现类应直接实现 org.springframework.ai.chat.memory.ChatMemory

}
