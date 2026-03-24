# Lab04 - Chat Memory（对话记忆）

> 本 Lab 演示如何使用 Spring AI 的 **Advisor 模式** 实现对话记忆功能。

---

## 一、概述

对话记忆是多轮对话系统的核心能力。本 Lab 通过 Spring AI 的 `MessageChatMemoryAdvisor` 实现：
- **自动加载历史**：请求前从存储加载历史消息
- **自动保存对话**：响应后将新对话保存到存储
- **多存储后端**：支持内存、Redis、JDBC 三种策略

---

## 二、Advisor 模式原理解析

### 2.1 什么是 Advisor 模式？

Advisor 模式是 Spring AI 提供的 **AOP（面向切面编程）** 风格的拦截机制，用于在 ChatClient 调用链中插入横切关注点。

```
┌─────────────────────────────────────────────────────────────┐
│                     ChatClient 调用链                        │
├─────────────────────────────────────────────────────────────┤
│  1. 用户调用 chatClient.prompt().user("...").call()          │
│                         ↓                                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         MessageChatMemoryAdvisor (前置拦截)           │   │
│  │  • 根据 conversationId 从 ChatMemory 加载历史消息     │   │
│  │  • 将历史消息注入到 Prompt 上下文                     │   │
│  └──────────────────────────────────────────────────────┘   │
│                         ↓                                    │
│  2. 调用大模型（带有完整上下文的 Prompt）                     │
│                         ↓                                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         MessageChatMemoryAdvisor (后置拦截)           │   │
│  │  • 提取用户消息和 AI 回复                             │   │
│  │  • 将对话保存到 ChatMemory                            │   │
│  └──────────────────────────────────────────────────────┘   │
│                         ↓                                    │
│  3. 返回响应给用户                                           │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Advisor 模式的优势

| 对比项 | 手动管理方式 | Advisor 模式 |
|--------|-------------|-------------|
| **代码侵入性** | Service 层需手动加载/保存历史 | Service 层只需传递 conversationId |
| **关注点分离** | 业务逻辑与记忆管理耦合 | 记忆管理完全解耦 |
| **可扩展性** | 每个 Service 都需重复实现 | 一次配置，全局生效 |
| **可维护性** | 修改记忆逻辑需改多处 | 只需修改 Advisor 配置 |

### 2.3 核心组件

#### ChatMemory 接口
```java
public interface ChatMemory {
    void add(String conversationId, List<Message> messages);
    List<Message> get(String conversationId);
    void clear(String conversationId);
}
```

#### MessageChatMemoryAdvisor
```java
// 配置 ChatClient 时注入 Advisor（使用 Builder 模式）
ChatClient chatClient = ChatClient.builder(chatModel)
    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
    .build();
```

#### Service 层使用
```java
// 通过 advisors 参数传递会话 ID
String response = chatClient.prompt()
    .user(content)
    .advisors(spec -> spec.param("chat_memory_conversation_id", conversationId))
    .call()
    .content();
```

---

## 三、项目结构

```
lab04-chat-memory/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/.../lab04chatmemory/
    │   │   ├── Lab04ChatMemoryApplication.java
    │   │   ├── config/
    │   │   │   └── MemoryChatClientConfig.java  # ChatClient 配置（注入 Advisor）
    │   │   ├── controller/
    │   │   │   └── ChatController.java
    │   │   ├── dto/
    │   │   │   ├── ChatRequestDTO.java
    │   │   │   └── ChatResponseDTO.java
    │   │   ├── memory/
    │   │   │   ├── InMemoryChatMemoryStore.java  # 内存存储
    │   │   │   ├── RedisChatMemoryStore.java     # Redis 存储
    │   │   │   └── JdbcChatMemoryStore.java      # JDBC 存储
    │   │   └── service/
    │   │       ├── ChatService.java
    │   │       └── impl/
    │   │           └── ChatServiceImpl.java
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/
    │           └── V1__init_chat_memory.sql
    └── test/
        └── java/.../memory/
            ├── InMemoryChatMemoryStoreTest.java
            ├── RedisChatMemoryStoreTest.java
            └── JdbcChatMemoryStoreTest.java
```

---

## 四、存储策略

### 4.1 内存存储（默认）

适用于开发测试环境，数据存储在 JVM 内存中。

```yaml
spring:
  ai:
    chat:
      memory:
        type: memory  # 默认值
```

### 4.2 Redis 存储

适用于高并发分布式场景，支持跨实例共享记忆。

```yaml
spring:
  ai:
    chat:
      memory:
        type: redis
        redis-key-prefix: "chat:memory:"
  data:
    redis:
      host: localhost
      port: 6379
```

### 4.3 JDBC 存储

适用于企业级持久化场景，数据存储在关系型数据库。

```yaml
spring:
  ai:
    chat:
      memory:
        type: jdbc
  datasource:
    url: jdbc:mysql://localhost:3306/spring_ai_guide
    username: root
    password: root
  flyway:
    enabled: true
```

---

## 五、API 接口

### 聊天接口

**请求**
```http
POST /lab04/chat
Content-Type: application/json

{
  "sessionId": "user-123",
  "content": "你好，请介绍一下 Spring AI"
}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "Spring AI 是一个...",
    "sessionId": "user-123",
    "messageCount": 2,
    "timestamp": 1710000000000
  }
}
```

---

## 六、运行方式

### 1. 环境准备

```bash
# 设置 API Key
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://api.siliconflow.cn
export OPENAI_MODEL=Qwen/Qwen3-8B
```

### 2. 启动应用

```bash
cd lab04-chat-memory
mvn spring-boot:run
```

### 3. 测试对话

```bash
# 第一次对话
curl -X POST http://localhost:8084/lab04/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-1","content":"我叫张三"}'

# 第二次对话（AI 应能记住你的名字）
curl -X POST http://localhost:8084/lab04/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-1","content":"我叫什么名字？"}'
```

---

## 七、关键代码解析

### 7.1 ChatClient 配置

```java
@Configuration
public class MemoryChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
```

### 7.2 Service 层实现

```java
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final String CONVERSATION_ID_KEY = "chat_memory_conversation_id";

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Override
    public ChatResponseDTO chat(String sessionId, String content) {
        // Advisor 自动管理记忆，Service 层只需传递会话 ID
        String response = chatClient.prompt()
                .user(content)
                .advisors(spec -> spec.param(CONVERSATION_ID_KEY, sessionId))
                .call()
                .content();

        int messageCount = chatMemory.get(sessionId).size();
        return ChatResponseDTO.of(response, sessionId, messageCount);
    }
}
```

---

## 八、总结

本 Lab 展示了如何使用 Spring AI 的 Advisor 模式实现对话记忆功能：

1. **解耦记忆管理**：Service 层不再关心历史消息的加载和保存
2. **声明式配置**：通过配置 ChatClient 注入 Advisor 即可启用记忆功能
3. **灵活的存储策略**：支持内存、Redis、JDBC 三种存储后端
4. **AOP 思想**：Advisor 作为拦截器，在请求前后自动处理记忆逻辑

这种模式符合 Spring 的设计哲学，让开发者专注于业务逻辑而非基础设施代码。
