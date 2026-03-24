# Lab05 - Official Chat Memory（官方对话记忆接口实践）

> 本 Lab 演示如何使用 Spring AI 官方 `ChatMemory` 接口实现对话记忆，对比 Lab04 的自定义实现方案。

---

## 一、概述

Lab05 与 Lab04 形成鲜明对比：
- **Lab04**：自定义存储接口，手动实现多种存储后端
- **Lab05**：直接实现 `ChatMemory` 接口，代码更简洁

本 Lab 实现特点：
- **直接实现接口**：不依赖额外的 Starter，直接实现 `ChatMemory`
- **自动建表**：启动时自动创建数据库表
- **滑动窗口**：内置消息数量限制，避免上下文过长
- **极简代码**：仅需一个 `JdbcChatMemory` 类

---

## 二、与 Lab04 的对比

### 2.1 核心差异对比表

| 维度 | Lab04（自定义实现） | Lab05（接口实现） |
|------|-------------------|------------------|
| **学习价值** | 理解多存储后端设计 | 掌握接口直接实现 |
| **代码量** | 较多（多个 Store 类） | 较少（单个类） |
| **灵活性** | 支持切换存储后端 | 专注 JDBC 一种 |
| **复杂度** | 条件装配、多配置 | 单一配置 |

### 2.2 代码量对比

#### Lab04 需要的文件
```
lab04-chat-memory/
├── memory/
│   ├── InMemoryChatMemoryStore.java   # ~60 行
│   ├── RedisChatMemoryStore.java      # ~90 行
│   └── JdbcChatMemoryStore.java       # ~130 行
├── config/
│   └── ChatMemoryConfig.java          # ~60 行（条件装配）
└── db/migration/
    └── V1__init_chat_memory.sql       # ~20 行
```

#### Lab05 需要的文件
```
lab05-official-chat-memory/
├── memory/
│   └── JdbcChatMemory.java            # ~130 行（含建表、滑动窗口）
└── config/
    └── OfficialMemoryConfig.java      # ~30 行
```

**结论**：Lab05 代码量减少约 **50%**。

---

## 三、项目结构

```
lab05-official-chat-memory/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/.../lab05officialchatmemory/
    │   │   ├── Lab05OfficialChatMemoryApplication.java
    │   │   ├── config/
    │   │   │   └── OfficialMemoryConfig.java
    │   │   ├── controller/
    │   │   │   └── ChatController.java
    │   │   ├── dto/
    │   │   │   ├── ChatRequestDTO.java
    │   │   │   └── ChatResponseDTO.java
    │   │   ├── memory/
    │   │   │   └── JdbcChatMemory.java      # 核心：实现 ChatMemory 接口
    │   │   └── service/
    │   │       ├── ChatService.java
    │   │       └── impl/
    │   │           └── ChatServiceImpl.java
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── resources/
        │   └── application.yml              # H2 测试配置
        └── java/...
            ├── repository/
            │   └── ChatMemoryTableTest.java # 验证表创建
            └── service/
                └── ChatServiceIntegrationTest.java
```

---

## 四、核心代码解析

### 4.1 JdbcChatMemory 实现

```java
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

    private final JdbcTemplate jdbcTemplate;
    private final int maxMessages = 20;  // 滑动窗口大小

    public JdbcChatMemory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initTable();  // 启动时自动建表
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            jdbcTemplate.update(INSERT_SQL,
                conversationId,
                message.getMessageType().name(),
                message.getText()
            );
        }
        trimMessages(conversationId);  // 滑动窗口裁剪
    }

    @Override
    public List<Message> get(String conversationId) {
        // 从数据库加载历史消息
    }

    @Override
    public void clear(String conversationId) {
        jdbcTemplate.update(DELETE_SQL, conversationId);
    }
}
```

**关键特性**：
- 实现 `ChatMemory` 接口的三个方法：`add`、`get`、`clear`
- 构造函数中自动建表
- 内置滑动窗口，限制消息数量

### 4.2 ChatClient 配置

```java
@Configuration
public class OfficialMemoryConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
```

**关键点**：
- `ChatMemory` 由 `JdbcChatMemory` 自动注入
- `MessageChatMemoryAdvisor` 自动管理记忆加载和保存

### 4.3 Service 层使用

```java
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Override
    public ChatResponseDTO chat(String sessionId, String content) {
        String response = chatClient.prompt()
                .user(content)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();

        int messageCount = chatMemory.get(sessionId).size();
        return ChatResponseDTO.of(response, sessionId, messageCount);
    }
}
```

---

## 五、数据库表结构

自动创建的 `chat_memory` 表：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `conversation_id` | VARCHAR(255) | 会话 ID |
| `message_type` | VARCHAR(20) | 消息类型（USER/ASSISTANT） |
| `content` | TEXT | 消息内容 |
| `created_at` | TIMESTAMP | 创建时间 |

---

## 六、API 接口

### 聊天接口

**请求**
```http
POST /lab05/chat
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

## 七、运行方式

### 1. 环境准备

```bash
# 设置 API Key
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://api.siliconflow.cn
export OPENAI_MODEL=Qwen/Qwen3-8B

# 创建数据库（MySQL）
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS spring_ai_guide;"
```

### 2. 启动应用

```bash
cd lab05-official-chat-memory
mvn spring-boot:run
```

### 3. 测试对话

```bash
# 第一次对话
curl -X POST http://localhost:8085/lab05/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-1","content":"我叫张三"}'

# 第二次对话（AI 应能记住你的名字）
curl -X POST http://localhost:8085/lab05/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-1","content":"我叫什么名字？"}'
```

### 4. 验证数据库

```sql
-- 查看自动创建的表
SHOW TABLES LIKE 'chat_memory';

-- 查看存储的对话记录
SELECT * FROM chat_memory WHERE conversation_id = 'test-1';
```

---

## 八、测试验证

### 8.1 集成测试（H2 内存数据库）

```java
@SpringBootTest
@ActiveProfiles("test")
class ChatServiceIntegrationTest {

    @Autowired
    private ChatMemory chatMemory;

    @Test
    @DisplayName("验证对话记忆持久化")
    void shouldPersistChatMemory_whenChat() {
        List<Message> messages = List.of(
                new UserMessage("我叫张三"),
                new AssistantMessage("你好张三！")
        );
        chatMemory.add(TEST_SESSION_ID, messages);

        List<Message> storedMessages = chatMemory.get(TEST_SESSION_ID);
        assertEquals(2, storedMessages.size());
    }
}
```

### 8.2 运行测试

```bash
mvn test -pl lab05-official-chat-memory
```

---

## 九、总结

本 Lab 展示了直接实现 `ChatMemory` 接口的简洁方案：

| 特性 | 说明 |
|------|------|
| **代码精简** | 单个类实现全部功能 |
| **自动建表** | 启动时自动创建数据库表 |
| **滑动窗口** | 内置消息数量限制 |
| **易于理解** | 直接实现接口，无额外抽象 |

**最佳实践建议**：
1. 学习阶段：先学 Lab04 理解多存储后端设计
2. 生产阶段：根据需求选择 Lab04 或 Lab05 方案
3. 快速原型：使用 Lab05 快速实现对话记忆

---

## 十、与 Lab04 的选择建议

### 选择 Lab04 的场景
- 需要支持多种存储后端（内存/Redis/JDBC）
- 需要运行时切换存储类型
- 团队需要学习条件装配等高级 Spring 特性

### 选择 Lab05 的场景
- 只需要 JDBC 存储
- 追求代码简洁
- 快速实现功能原型
