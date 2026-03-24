# Lab02 - Prompt Templates: 提示词模板实战

> 本 Lab 演示如何使用 Spring AI 的 Prompt API 实现提示词与代码解耦。
>
> **官方文档**：https://docs.spring.io/spring-ai/reference/api/prompt.html

---

## 一、学习目标

通过本 Lab，你将掌握：

- 为什么要使用 PromptTemplate（提示词与代码解耦）
- `PromptTemplate` 与 `SystemPromptTemplate` 的区别与使用场景
- 如何创建和管理外部模板文件
- 如何构建多消息 `Prompt`（系统消息 + 用户消息）

---

## 二、核心概念

### 2.1 Prompt API 核心组件

| 组件 | 作用 | 官方示例 | 使用场景 |
|------|------|----------|----------|
| `PromptTemplate` | 构建用户提示 | `Tell me a {adjective} joke` | 单轮对话、用户输入模板化 |
| `SystemPromptTemplate` | 构建系统角色/人设 | `You are a helpful AI assistant named {name}` | 角色扮演、行为约束 |
| `Prompt` | 组合多条消息 | `new Prompt(List.of(systemMessage, userMessage))` | 多轮对话、复杂提示 |

### 2.2 官方代码示例

#### 使用 PromptTemplate（用户消息模板）

```java
// 官方示例：https://docs.spring.io/spring-ai/reference/api/prompt.html
PromptTemplate promptTemplate = new PromptTemplate(templateContent);
String prompt = promptTemplate.render(Map.of("name", name, "topic", topic));
```

#### 使用 SystemPromptTemplate（系统角色模板）

```java
// 官方示例：使用 Resource 加载外部模板
SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

// 组合系统消息与用户消息
Prompt prompt = new Prompt(List.of(systemMessage, new UserMessage(question)));
```

### 2.3 架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                     resources/prompts/*.st                           │
│  ┌─────────────────────────┐    ┌─────────────────────────┐         │
│  │     joke.st             │    │   system-assistant.st   │         │
│  │  "讲一个关于{name}的笑话" │    │  "Your name is {name}"  │         │
│  └─────────────────────────┘    └─────────────────────────┘         │
└─────────────────────────────────────────────────────────────────────┘
                    ↓                              ↓
┌─────────────────────────────────────────────────────────────────────┐
│                          Java 代码                                   │
│  ┌─────────────────────┐              ┌─────────────────────────┐   │
│  │   PromptTemplate    │              │  SystemPromptTemplate   │   │
│  │  (用户消息模板)       │              │   (系统角色模板)         │   │
│  └─────────────────────┘              └─────────────────────────┘   │
│            ↓                                       ↓                  │
│  ┌─────────────────────┐              ┌─────────────────────────┐   │
│  │    UserMessage      │              │    SystemMessage        │   │
│  └─────────────────────┘              └─────────────────────────┘   │
│            ↓                                       ↓                  │
│            └───────────────────┬───────────────────┘                  │
│                              ↓                                       │
│                    ┌─────────────────────┐                          │
│                    │       Prompt        │                          │
│                    │  List<Message>      │                          │
│                    └─────────────────────┘                          │
│                              ↓                                       │
│                    ┌─────────────────────┐                          │
│                    │     ChatModel       │                          │
│                    └─────────────────────┘                          │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 三、项目结构

```
lab02-prompt-templates/
├── src/
│   ├── main/
│   │   ├── java/com/gyreq/ai/example/lab02prompttemplates/
│   │   │   ├── Lab02PromptTemplatesApplication.java  # 启动类
│   │   │   ├── controller/
│   │   │   │   ├── JokeController.java               # 笑话控制器
│   │   │   │   └── AssistantController.java          # 助手控制器 ⭐
│   │   │   ├── service/
│   │   │   │   ├── JokeService.java                  # 笑话服务接口
│   │   │   │   ├── AssistantService.java             # 助手服务接口 ⭐
│   │   │   │   └── impl/
│   │   │   │       ├── JokeServiceImpl.java          # 笑话服务实现
│   │   │   │       └── AssistantServiceImpl.java     # 助手服务实现 ⭐
│   │   │   ├── dto/
│   │   │   │   ├── JokeResponseDTO.java              # 笑话响应 DTO
│   │   │   │   └── AssistantResponseDTO.java         # 助手响应 DTO ⭐
│   │   │   └── exception/
│   │   │       └── TemplateResourceException.java    # 自定义异常
│   │   └── resources/
│   │       ├── prompts/
│   │       │   ├── joke.st                           # 笑话模板 ⭐
│   │       │   └── system-assistant.st               # 系统助手模板 ⭐
│   │       └── application.yml                       # 配置文件
│   └── test/
│       └── java/.../service/
│           ├── JokeServiceTest.java                  # 笑话服务测试
│           └── AssistantServiceTest.java             # 助手服务测试 ⭐
└── pom.xml
```

---

## 四、API 接口说明

### 4.1 生成笑话接口（PromptTemplate 示例）

**请求**：
```http
GET /lab02/joke?name=小明&topic=程序员
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "joke": "小明写代码从不写注释，同事问他为什么，他说：我的代码自解释！",
    "name": "小明",
    "topic": "程序员",
    "timestamp": 1710000000000
  }
}
```

### 4.2 角色扮演助手接口（SystemPromptTemplate 示例）⭐

**请求**：
```http
GET /lab02/assistant?name=Jarvis&voice=莎士比亚&question=请介绍一下自己
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| name | String | 是 | 助手名字 | `Jarvis`、`小助手` |
| voice | String | 是 | 助手风格 | `莎士比亚`、`科幻小说家`、`幽默大师` |
| question | String | 是 | 用户问题 | `请介绍一下自己` |

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "吾乃 Jarvis，一位以莎士比亚风格言说之助手。汝问吾名，吾当以诗答之...",
    "name": "Jarvis",
    "voice": "莎士比亚",
    "question": "请介绍一下自己",
    "timestamp": 1710000000000
  }
}
```

---

## 五、核心代码解析

### 5.1 PromptTemplate 使用（JokeServiceImpl）

```java
@Service
public class JokeServiceImpl implements JokeService {

    private final ChatClient chatClient;
    private final Resource jokeTemplateResource;

    public JokeServiceImpl(
            ChatClient chatClient,
            AiProperties aiProperties,
            @Value("classpath:/prompts/joke.st") Resource jokeTemplateResource) {
        this.chatClient = chatClient;
        this.jokeTemplateResource = jokeTemplateResource;
    }

    @Override
    public JokeResponseDTO generateJoke(String name, String topic) {
        // 1. 加载模板文件
        String templateContent = loadTemplateContent();

        // 2. 创建 PromptTemplate 并填充参数
        PromptTemplate promptTemplate = new PromptTemplate(templateContent);
        String prompt = promptTemplate.render(Map.of(
                "name", name,
                "topic", topic
        ));

        // 3. 调用 AI 模型
        String joke = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return JokeResponseDTO.of(joke, name, topic);
    }
}
```

### 5.2 SystemPromptTemplate 使用（AssistantServiceImpl）⭐

```java
@Service
public class AssistantServiceImpl implements AssistantService {

    private final ChatModel chatModel;
    private final Resource systemAssistantTemplateResource;

    public AssistantServiceImpl(
            ChatModel chatModel,
            AiProperties aiProperties,
            @Value("classpath:/prompts/system-assistant.st") Resource systemAssistantTemplateResource) {
        this.chatModel = chatModel;
        this.systemAssistantTemplateResource = systemAssistantTemplateResource;
    }

    @Override
    public AssistantResponseDTO chat(String name, String voice, String question) {
        // 1. 加载系统模板文件
        String systemTemplateContent = loadTemplateContent();

        // 2. 使用 SystemPromptTemplate 创建系统消息
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemTemplateContent);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of(
                "name", name,
                "voice", voice
        ));

        // 3. 创建用户消息
        UserMessage userMessage = new UserMessage(question);

        // 4. 组合成 Prompt 发送给模型
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        return AssistantResponseDTO.of(response, name, voice, question);
    }
}
```

**关键点对比**：

| 特性 | PromptTemplate | SystemPromptTemplate |
|------|----------------|---------------------|
| 消息类型 | 生成用户消息字符串 | 生成 SystemMessage |
| 适用场景 | 单轮对话、简单提示 | 角色扮演、行为约束 |
| API 风格 | `render()` 返回 String | `createMessage()` 返回 Message |
| 与 ChatClient 配合 | `.user(prompt)` | 需要 ChatModel + Prompt |

---

## 六、模板文件说明

### 6.1 模板文件位置

所有模板文件统一放在 `src/main/resources/prompts/` 目录下：

| 文件名 | 用途 | 参数 |
|--------|------|------|
| `joke.st` | 笑话生成模板 | `{name}`, `{topic}` |
| `system-assistant.st` | 系统助手角色模板 | `{name}`, `{voice}` |

### 6.2 模板语法

Spring AI 默认使用 **StringTemplate (ST4)** 语法：

```
占位符格式：{变量名}
示例：Your name is {name}.
```

### 6.3 为什么要把模板放到 resources 目录下？

| 优势 | 说明 |
|------|------|
| **便于修改** | 修改提示词无需重新编译代码，只需替换配置文件 |
| **版本管理** | 模板变更可通过 Git 追踪，支持回滚和对比 |
| **多语言支持** | 按语言创建不同模板（如 `joke_zh.st`、`joke_en.st`） |
| **协作友好** | 非技术人员（产品、运营）可独立维护提示词 |
| **环境隔离** | 不同环境可使用不同模板（dev/test/prod） |

---

## 七、运行方法

### 7.1 环境准备

| 软件 | 版本要求 |
|------|----------|
| JDK | 21+ |
| Maven | 3.9+ |

### 7.2 配置 API Key

```bash
# 设置环境变量
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://api.siliconflow.cn  # 可选，使用代理
```

### 7.3 启动应用

```bash
# 进入 lab02 目录
cd lab02-prompt-templates

# 启动应用
mvn spring-boot:run
```

启动成功后，应用运行在 `http://localhost:8082`。

### 7.4 测试接口

```bash
# 生成笑话（PromptTemplate 示例）
curl "http://localhost:8082/lab02/joke?name=小明&topic=程序员"

# 角色扮演对话（SystemPromptTemplate 示例）
curl "http://localhost:8082/lab02/assistant?name=Jarvis&voice=莎士比亚&question=请介绍一下自己"

# 尝试不同风格
curl "http://localhost:8082/lab02/assistant?name=AI助手&voice=幽默大师&question=今天天气怎么样"
```

---

## 八、单元测试

### 8.1 运行测试

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=JokeServiceTest
mvn test -Dtest=AssistantServiceTest
```

### 8.2 测试覆盖场景

| 测试类 | 覆盖场景 |
|--------|----------|
| `JokeServiceTest` | PromptTemplate 参数替换、异常处理 |
| `AssistantServiceTest` | SystemPromptTemplate 系统消息构建、多消息 Prompt 组合 |

---

## 九、最佳实践

### 9.1 模板文件管理

| 实践 | 说明 |
|------|------|
| 统一目录 | 所有模板放在 `resources/prompts/` 下 |
| 命名规范 | 使用 `.st` 后缀，如 `joke.st`、`system-assistant.st` |
| 版本控制 | 模板文件纳入 Git 管理 |
| 注释说明 | 模板文件头部注释说明用途和参数 |

### 9.2 选择合适的模板类型

| 场景 | 推荐方案 |
|------|----------|
| 简单的用户输入模板化 | `PromptTemplate` + `ChatClient` |
| 需要设置系统角色/人设 | `SystemPromptTemplate` + `ChatModel` + `Prompt` |
| 多轮对话 | 组合多条 Message 构建 Prompt |

### 9.3 参数校验

```java
// Controller 层参数校验
@GetMapping("/assistant")
public Result<AssistantResponseDTO> chat(
        @RequestParam @NotBlank(message = "名字不能为空") String name,
        @RequestParam @NotBlank(message = "风格不能为空") String voice,
        @RequestParam @NotBlank(message = "问题不能为空") String question) {
    // ...
}
```

---

## 十、下一步

完成本 Lab 后，建议继续学习：

- **Lab03 - 结构化输出**：让 AI 返回结构化的 JSON 数据
- **Lab04 - 对话记忆**：实现多轮对话的上下文管理
- **Lab05 - 工具调用**：让 AI 调用外部工具/API

---

## 参考资料

- [Spring AI Prompt API 官方文档](https://docs.spring.io/spring-ai/reference/api/prompt.html)
- [StringTemplate (ST4) 语法参考](https://github.com/antlr/stringtemplate4)
