# Spring AI Guide - 项目规范与开发指南

> 本文档既是给开发者的学习指南，也是给 AI 助手（Claude）的工作说明书。后续所有 Lab 的开发都应严格遵循本文档定义的规范。

---

## 一、项目概述

**spring-ai-guide** 是一个由浅入深的 Spring AI 学习项目，包含多个独立的 Lab 模块。目标是通过实战代码帮助开发者掌握 Spring AI 的核心能力。

### 技术栈
| 组件 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.4.x |
| Spring AI | 1.1.3 |
| Maven | 3.9.x |
| JUnit | 5.11.x |
| Mockito | 5.14.x |

### 代码规范
严格遵循《阿里巴巴 Java 开发手册》（最新版），并使用 PMD 插件进行静态检查。

---

## 二、模块命名规范

### 父工程
- **groupId**: `com.gyreq.ai.example`
- **artifactId**: `spring-ai-guide`
- **version**: `1.0.0-SNAPSHOT`

### 公共模块 (`guide-common`)

所有 Lab 模块**必须**依赖 `guide-common` 公共模块，该模块提供：

| 类 | 说明 |
|------|------|
| `com.gyreq.ai.example.common.model.Result<T>` | 统一响应格式封装 |
| `com.gyreq.ai.example.common.exception.BusinessException` | 业务异常基类 |
| `com.gyreq.ai.example.common.exception.GlobalExceptionHandler` | 全局异常处理器 |
| `com.gyreq.ai.example.common.config.AiProperties` | AI 模型配置属性 |
| `com.gyreq.ai.example.common.config.ChatClientConfig` | ChatClient Bean 配置 |
| `com.gyreq.ai.example.common.test.BaseChatClientTest` | 测试基类（提供 Mock ChatClient） |

**依赖方式**：
```xml
<dependency>
    <groupId>com.gyreq.ai.example</groupId>
    <artifactId>guide-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Lab 模块命名
```
lab{序号}-{主题}
```

| 序号 | 模块名 | 主题 |
|------|--------|------|
| 01 | lab01-helloworld | Spring AI 快速入门 |
| 02 | lab02-prompt-templates | 提示词模板 |
| 03 | lab03-structured-output | 结构化输出 |
| 04 | lab04-memory | 对话记忆 |
| 05 | lab05-tool-calling | 工具调用 |

### 包命名规范
```
com.gyreq.ai.example.{模块名}
```

示例：
```
com.gyreq.ai.example.lab01helloworld      # Lab01 的根包
com.gyreq.ai.example.lab01helloworld.controller
com.gyreq.ai.example.lab01helloworld.service
com.gyreq.ai.example.lab01helloworld.service.impl
com.gyreq.ai.example.lab01helloworld.dto
```

### 类命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Controller | `{业务}Controller` | `ChatController` |
| Service 接口 | `{业务}Service` | `ChatService` |
| Service 实现 | `{业务}ServiceImpl` | `ChatServiceImpl` |
| DTO | `{业务}{用途}DTO` | `ChatRequestDTO`、`ChatResponseDTO` |
| Entity | `{业务}Entity` | `MessageEntity` |
| Repository | `{业务}Repository` | `MessageRepository` |
| 业务异常 | `{业务}Exception` | `TemplateResourceException` |
| 测试类 | `{被测类}Test` | `ChatServiceTest` |

---

## 三、公共模块使用指南

### 3.1 统一响应格式

所有 Controller 返回值必须使用 `Result<T>` 包装：

```java
@GetMapping("/chat")
public Result<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
    ChatResponseDTO response = chatService.chat(request.message());
    return Result.success(response);
}
```

**Result 类定义**：
```java
public record Result<T>(int code, String message, T data) {
    public static <T> Result<T> success(T data);     // 成功响应
    public static <T> Result<T> success();           // 成功响应（无数据）
    public static <T> Result<T> error(String message);        // 失败响应
    public static <T> Result<T> error(int code, String message);  // 失败响应（自定义码）
}
```

### 3.2 异常处理

**业务异常**：继承 `BusinessException`

```java
public class TemplateResourceException extends BusinessException {
    public TemplateResourceException(String message) {
        super("服务配置错误：" + message);
    }
}
```

**全局异常处理**：`guide-common` 已提供 `GlobalExceptionHandler`，自动处理：
- `BusinessException` → 业务异常
- `MethodArgumentNotValidException` → 参数校验异常
- `IllegalArgumentException` → 非法参数异常
- `Exception` → 未知异常

### 3.3 AI 配置

`guide-common` 已提供 `ChatClient` Bean，Lab 模块**无需**再配置。

使用方式：
```java
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;      // 自动注入
    private final AiProperties aiProperties;  // 模型配置属性

    @Override
    public ChatResponseDTO chat(String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return ChatResponseDTO.of(response, aiProperties.getModel());
    }
}
```

### 3.4 测试基类

Service 层测试继承 `BaseChatClientTest`：

```java
class ChatServiceTest extends BaseChatClientTest {

    @Mock
    private AiProperties aiProperties;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(aiProperties.getModel()).thenReturn("test-model");
        chatService = new ChatServiceImpl(chatClient, aiProperties);
    }

    @Test
    void shouldReturnResponse_whenChat() {
        // 使用基类提供的 mockCallResponse 方法
        mockCallResponse("expected response");

        ChatResponseDTO response = chatService.chat("hello");

        assertEquals("expected response", response.content());
    }
}
```

**基类提供的方法**：
| 方法 | 说明 |
|------|------|
| `mockCallResponse(String)` | 配置同步调用 Mock 链 |
| `mockStreamResponse(Flux<String>)` | 配置流式调用 Mock 链 |
| `chatClient` | 已 Mock 的 ChatClient |
| `requestSpec` | 已 Mock 的 ChatClientRequestSpec |
| `callResponseSpec` | 已 Mock 的 CallResponseSpec |
| `streamResponseSpec` | 已 Mock 的 StreamResponseSpec |

---

## 四、代码分层规范

严格遵循阿里巴巴分层架构，各层职责如下：

```
┌─────────────────────────────────────────────────────────┐
│                    Controller 层                        │
│         接收请求、参数校验、调用 Service、返回响应          │
├─────────────────────────────────────────────────────────┤
│                    Service 层                           │
│       业务逻辑处理、事务控制、调用外部服务/AI 模型          │
├─────────────────────────────────────────────────────────┤
│                    Repository 层                        │
│              数据访问、持久化操作                         │
├─────────────────────────────────────────────────────────┤
│                    External 层                          │
│         外部服务调用（AI 模型、第三方 API）                │
└─────────────────────────────────────────────────────────┘
```

### 各层规范

#### Controller 层
- 仅负责接收 HTTP 请求，返回响应
- **禁止**在 Controller 中编写业务逻辑
- 参数校验使用 JSR-303 注解（`@Valid`, `@NotNull` 等）
- 统一返回格式：`Result<T>` 包装类
- 异常统一由 `GlobalExceptionHandler` 处理（guide-common 已提供）

#### Service 层
- 核心业务逻辑层
- 必须定义接口（`XxxService`）和实现类（`XxxServiceImpl`）
- 复杂业务逻辑应拆分为多个 Service 协作
- **必须**有单元测试覆盖

#### Repository 层
- 数据访问层，使用 Spring Data JPA 或 MyBatis
- Repository 仅包含数据访问逻辑，**禁止**包含业务逻辑

### 目录结构示例

```
lab01-helloworld/
├── pom.xml                                          # 依赖 guide-common
├── README.md
└── src/
    ├── main/
    │   ├── java/com/gyreq/ai/example/lab01helloworld/
    │   │   ├── Lab01HelloworldApplication.java      # 启动类
    │   │   ├── controller/
    │   │   │   └── ChatController.java              # 控制器
    │   │   ├── service/
    │   │   │   ├── ChatService.java                 # 服务接口
    │   │   │   └── impl/
    │   │   │       └── ChatServiceImpl.java         # 服务实现
    │   │   ├── dto/
    │   │   │   ├── ChatRequestDTO.java              # 请求 DTO
    │   │   │   └── ChatResponseDTO.java             # 响应 DTO
    │   │   └── exception/                            # 业务异常（可选）
    │   │       └── XxxException.java
    │   └── resources/
    │       ├── application.yml
    │       └── prompts/                              # 模板文件（可选）
    │           └── xxx.st
    └── test/
        └── java/.../service/
            └── ChatServiceTest.java                  # 单元测试（继承 BaseChatClientTest）
```

**注意**：
- ❌ 不需要 `common/` 目录（Result、GlobalExceptionHandler 由 guide-common 提供）
- ❌ 不需要 `config/AiConfig.java`（ChatClient Bean 由 guide-common 提供）
- ❌ 不需要 `config/AiProperties.java`（由 guide-common 提供）

---

## 五、Lab 规划路线图

### Lab 01: Spring AI 快速入门 (`lab01-helloworld`)

**目标**：搭建第一个 Spring AI 应用，理解核心概念

**核心知识点**：
- Spring AI 项目结构
- `spring-ai-starter-model-openai` 使用
- `ChatClient` 基本用法
- 同步调用 vs 流式调用

---

### Lab 02: 提示词模板 (`lab02-prompt-templates`)

**目标**：掌握 PromptTemplate 的使用

**核心知识点**：
- `PromptTemplate` 使用
- 外部模板文件管理
- 动态参数填充

---

### Lab 03: 结构化输出 (`lab03-structured-output`)

**目标**：让 AI 返回结构化数据（JSON、POJO）

**核心知识点**：
- `BeanOutputConverter` 使用
- JSON Schema 自动生成
- 自定义实体映射

---

### Lab 04: 对话记忆 (`lab04-memory`)

**目标**：实现多轮对话的上下文管理

**核心知识点**：
- `ChatMemory` 接口
- 会话管理
- 持久化记忆

---

### Lab 05: 工具调用 (`lab05-tool-calling`)

**目标**：让 AI 调用外部工具/API

**核心知识点**：
- `@Tool` 注解使用
- Function Calling 流程
- 自定义工具函数

---

## 六、测试规范

### 测试框架
- **JUnit 5**：单元测试框架
- **Mockito**：Mock 框架
- **BaseChatClientTest**：公共测试基类（guide-common 提供）

### 测试覆盖要求

| 层级 | 覆盖率要求 | 说明 |
|------|-----------|------|
| Service 层 | **100%** | 必须有单元测试 |
| Controller 层 | 可选 | 可用 `@WebMvcTest` |

### 测试命名规范

```java
// 测试类命名：{被测类}Test
class ChatServiceTest extends BaseChatClientTest { }

// 测试方法命名：should{期望行为}_when{条件}
@Test
@DisplayName("发送消息时应返回 AI 响应")
void shouldReturnAiResponse_whenSendMessage() { }
```

---

## 七、配置规范

### application.yml 结构

```yaml
server:
  port: 8081  # 每个 Lab 使用不同端口

spring:
  application:
    name: lab01-helloworld

  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: ${OPENAI_BASE_URL:https://api.siliconflow.cn}
      chat:
        options:
          model: ${OPENAI_MODEL:Qwen/Qwen3-8B}
          temperature: 0.7
          max-tokens: 1024

logging:
  level:
    com.gyreq: DEBUG
    org.springframework.ai: DEBUG
```

### 敏感信息处理
- API Key 等敏感信息**禁止**硬编码
- 使用环境变量：`${OPENAI_API_KEY}`

---

## 八、开发注意事项

### 必须遵守
1. **所有 Lab 必须依赖 guide-common**
2. **所有 Service 层必须有接口和实现类**
3. **所有 Service 层必须有单元测试**
4. **所有公共方法必须有 Javadoc**
5. **禁止使用 `System.out.println`，使用 SLF4J**
6. **禁止在循环中调用 AI API**
7. **敏感信息禁止提交到代码库**

### 推荐实践
1. 使用 Lombok 减少样板代码
2. 使用 Optional 避免 NPE
3. API 响应统一使用 `Result<T>` 包装
4. 测试类继承 `BaseChatClientTest`
