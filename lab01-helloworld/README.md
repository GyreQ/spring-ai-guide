# Lab01 - Hello World: Spring AI 快速入门

> 本 Lab 是 Spring AI 学习的第一站，带你从零搭建一个 AI 聊天应用。

---

## 一、学习目标

通过本 Lab，你将掌握：

- Spring AI 项目的基本结构
- `ChatClient` 的核心用法
- 同步调用 vs 流式调用的区别
- 如何配置 OpenAI 模型参数

---

## 二、核心概念

### 2.1 ChatClient 是什么？

`ChatClient` 是 Spring AI 提供的**高级 API**，用于与大语言模型进行交互。它封装了底层的 `ChatModel`，提供了流畅的链式调用接口。

```
┌─────────────────────────────────────────────────────┐
│                    ChatClient                        │
│  ┌─────────────────────────────────────────────┐   │
│  │  prompt() → user() → call() / stream()      │   │
│  └─────────────────────────────────────────────┘   │
│                       ↓                              │
│                 ┌───────────┐                        │
│                 │ ChatModel │  ← 底层模型接口         │
│                 └───────────┘                        │
│                       ↓                              │
│              ┌─────────────────┐                     │
│              │ OpenAI / Ollama │  ← 具体实现         │
│              └─────────────────┘                     │
└─────────────────────────────────────────────────────┘
```

**核心方法**：

| 方法 | 说明 |
|------|------|
| `prompt()` | 创建一个提示词构建器 |
| `user(String)` | 设置用户消息 |
| `call()` | 同步调用，等待完整响应 |
| `stream()` | 流式调用，实时返回响应 |

### 2.2 同步调用 vs 流式调用

| 特性 | 同步调用 (`call`) | 流式调用 (`stream`) |
|------|------------------|-------------------|
| 响应方式 | 等待完整响应后返回 | 逐 token 返回 |
| 用户体验 | 需等待较长时间 | 实时看到生成过程 |
| 适用场景 | 短文本、需要完整结果 | 长文本、实时展示 |
| 返回类型 | `String` | `Flux<String>` |

---

## 三、项目结构

```
lab01-helloworld/
├── src/
│   ├── main/
│   │   ├── java/com/gyreq/ai/example/lab01helloworld/
│   │   │   ├── Lab01HelloworldApplication.java  # 启动类
│   │   │   ├── config/
│   │   │   │   └── AiConfig.java                # AI 配置
│   │   │   ├── controller/
│   │   │   │   └── ChatController.java          # 控制器
│   │   │   ├── service/
│   │   │   │   ├── ChatService.java             # 服务接口
│   │   │   │   └── impl/
│   │   │   │       └── ChatServiceImpl.java     # 服务实现
│   │   │   ├── dto/
│   │   │   │   ├── ChatRequestDTO.java          # 请求 DTO
│   │   │   │   └── ChatResponseDTO.java         # 响应 DTO
│   │   │   └── common/
│   │   │       ├── Result.java                  # 统一响应
│   │   │       └── GlobalExceptionHandler.java  # 异常处理
│   │   └── resources/
│   │       └── application.yml                  # 配置文件
│   └── test/
│       └── java/com/gyreq/ai/example/lab01helloworld/
│           └── service/
│               └── ChatServiceTest.java         # 单元测试
└── pom.xml
```

---

## 四、核心代码解析

### 4.1 ChatServiceImpl - 核心业务逻辑

```java
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    @Override
    public ChatResponseDTO chat(String message) {
        // 使用 ChatClient 进行链式调用
        String response = chatClient.prompt()    // 1. 创建提示词构建器
                .user(message)                    // 2. 设置用户消息
                .call()                           // 3. 同步调用
                .content();                       // 4. 获取响应内容

        return ChatResponseDTO.of(response, "gpt-4o");
    }

    @Override
    public Flux<String> chatStream(String message) {
        // 流式调用，返回响应式流
        return chatClient.prompt()
                .user(message)
                .stream()                         // 流式调用
                .content();
    }
}
```

### 4.2 配置文件 application.yml

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}           # 从环境变量读取
      base-url: ${OPENAI_BASE_URL}         # 支持自定义代理
      chat:
        options:
          model: gpt-4o                     # 模型名称
          temperature: 0.7                  # 温度参数
          max-tokens: 1024                  # 最大 token 数
```

**参数说明**：

| 参数 | 说明 |
|------|------|
| `api-key` | OpenAI API Key，**禁止硬编码** |
| `base-url` | API 地址，可配置代理 |
| `model` | 模型名称，如 `gpt-4o`、`gpt-4o-mini` |
| `temperature` | 随机性控制，0-2，越小越确定 |
| `max-tokens` | 最大输出 token 数 |

---

## 五、API 接口说明

### 5.1 同步聊天接口

**请求**：
```http
POST /lab01/chat
Content-Type: application/json

{
  "message": "你好，介绍一下 Spring AI"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "Spring AI 是 Spring 生态系统中用于集成 AI 模型的框架...",
    "model": "gpt-4o",
    "timestamp": 1710000000000
  }
}
```

### 5.2 流式聊天接口

**请求**：
```http
POST /lab01/chat/stream
Content-Type: application/json

{
  "message": "写一首关于春天的诗"
}
```

**响应**（SSE 流）：
```
data:春

data:眠

data:不

data:觉

data:晓
```

---

## 六、运行方法

### 6.1 环境准备

| 软件 | 版本要求 |
|------|----------|
| JDK | 21+ |
| Maven | 3.9+ |

### 6.2 配置 API Key

**方式一：设置环境变量**
```bash
# Linux/macOS
export OPENAI_API_KEY=sk-xxx
export OPENAI_BASE_URL=https://api.openai.com  # 可选

# Windows PowerShell
$env:OPENAI_API_KEY="sk-xxx"
```

**方式二：使用 .env 文件（需配合插件）**

在项目根目录创建 `.env` 文件：
```properties
OPENAI_API_KEY=sk-xxx
OPENAI_BASE_URL=https://api.openai.com
```

### 6.3 启动应用

```bash
# 进入 lab01 目录
cd lab01-helloworld

# 启动应用
mvn spring-boot:run
```

启动成功后，应用运行在 `http://localhost:8081`。

### 6.4 测试接口

使用 curl 测试：
```bash
# 同步接口
curl -X POST http://localhost:8081/lab01/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'

# 流式接口
curl -X POST http://localhost:8081/lab01/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"message": "写一首诗"}'
```

---

## 七、单元测试解读

### 7.1 测试策略

本 Lab 的单元测试使用 **JUnit 5 + Mockito**：

- **JUnit 5**：测试框架，提供 `@Test`、`@DisplayName` 等注解
- **Mockito**：模拟框架，用于 Mock `ChatClient`，避免真实 API 调用

### 7.2 核心测试代码

```java
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private ChatService chatService;

    @Test
    @DisplayName("发送正常消息时应返回 AI 回复")
    void shouldReturnAiResponse_whenSendMessage() {
        // given: 配置 Mock 行为
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("expected response");

        // when: 调用方法
        ChatResponseDTO response = chatService.chat("test");

        // then: 验证结果
        assertEquals("expected response", response.content());

        // 验证调用链
        verify(chatClient).prompt();
        verify(requestSpec).user("test");
    }
}
```

### 7.3 运行测试

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=ChatServiceTest
```

---

## 八、常见问题

### Q1: 启动报错 "API key not found"

**原因**：未配置 `OPENAI_API_KEY` 环境变量。

**解决**：
```bash
export OPENAI_API_KEY=sk-xxx
```

### Q2: 如何使用国内代理？

**解决**：配置 `OPENAI_BASE_URL` 环境变量：
```bash
export OPENAI_BASE_URL=https://your-proxy.com
```

### Q3: 流式接口没有逐字显示？

**原因**：可能是客户端未正确处理 SSE 流。

**解决**：使用支持 SSE 的客户端，如 curl、Postman（开启 Stream）或前端 `EventSource` API。

### Q4: 如何切换到其他模型？

**解决**：修改 `application.yml` 中的 `model` 配置：
```yaml
spring:
  ai:
    openai:
      chat:
        options:
          model: gpt-4o-mini  # 或其他模型
```

---

## 九、下一步

完成本 Lab 后，建议继续学习：

- **Lab02 - 多模型支持**：学习如何配置和切换多个 AI 模型
- **Lab03 - 结构化输出**：让 AI 返回结构化的 JSON 数据
