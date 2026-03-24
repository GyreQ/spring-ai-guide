# Lab03 - Structured Output: 结构化输出实战

> 本 Lab 演示如何让大模型返回结构化的 Java 对象（POJO），而非自由文本。

---

## 一、学习目标

通过本 Lab，你将掌握：

- 为什么需要结构化输出
- `BeanOutputConverter` 的核心用法
- JSON Schema 自动生成机制
- 如何处理模型输出格式错误

---

## 二、核心概念

### 2.1 为什么需要结构化输出？

**问题场景**：大模型默认返回自由文本，程序难以直接处理。

```java
// ❌ 问题：模型返回自由文本，难以程序化处理
String response = "张三是28岁，他的爱好包括编程和打篮球";
// 如何提取 name、age、hobbies？
```

**解决方案**：让模型返回结构化的 JSON，再映射为 Java 对象。

```java
// ✅ 方案：模型返回结构化 JSON
String response = """
    {
        "name": "张三",
        "age": 28,
        "hobbies": ["编程", "打篮球"]
    }
    """;
// 可以直接反序列化为 PersonInfo 对象
```

### 2.2 技术方案对比

| 方案 | 优点 | 缺点 |
|------|------|------|
| **自己解析 JSON** | 灵活可控 | 需手动处理格式异常、写正则提取 |
| **使用 ChatClient.entity()** | 自动生成 Schema、自动映射、代码简洁 | 依赖 Spring AI 版本兼容性 |

**推荐**：使用 `ChatClient.entity()` 方法，它会：
1. 自动根据 POJO 类型生成 JSON Schema
2. 将 Schema 注入到请求中指导模型输出
3. 自动将模型响应解析为 POJO 对象

### 2.3 结构化输出架构

```
┌─────────────────────────────────────────────────────┐
│                PersonInfo.java (POJO)                │
│  - String name                                       │
│  - Integer age                                       │
│  - List<String> hobbies                              │
└─────────────────────────────────────────────────────┘
                         ↓ entity(PersonInfo.class)
┌─────────────────────────────────────────────────────┐
│                   JSON Schema (自动生成)              │
│  {                                                   │
│    "type": "object",                                 │
│    "properties": {                                   │
│      "name": { "type": "string" },                   │
│      "age": { "type": "integer" },                   │
│      "hobbies": { "type": "array", ... }             │
│    },                                                │
│    "required": ["name", "age", "hobbies"]            │
│  }                                                   │
└─────────────────────────────────────────────────────┘
                         ↓ 注入请求
┌─────────────────────────────────────────────────────┐
│                大模型输出 JSON                        │
│  {"name":"张三","age":28,"hobbies":["编程","打篮球"]}  │
└─────────────────────────────────────────────────────┘
                         ↓ 自动解析
┌─────────────────────────────────────────────────────┐
│              PersonInfo 对象                         │
│  PersonInfo(name="张三", age=28, hobbies=[...])     │
└─────────────────────────────────────────────────────┘
```

---

## 三、项目结构

```
lab03-structured-output/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/gyreq/ai/example/lab03structuredoutput/
    │   │   ├── Lab03StructuredOutputApplication.java
    │   │   ├── controller/
    │   │   │   └── ExtractController.java           # REST 控制器
    │   │   ├── service/
    │   │   │   ├── ExtractService.java              # 服务接口
    │   │   │   └── impl/
    │   │   │       └── ExtractServiceImpl.java      # 服务实现 ⭐
    │   │   ├── model/
    │   │   │   └── PersonInfo.java                  # POJO 定义
    │   │   ├── dto/
    │   │   │   ├── ExtractRequestDTO.java
    │   │   │   └── ExtractResponseDTO.java
    │   │   └── exception/
    │   │       └── StructuredOutputParseException.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/.../service/
            └── ExtractServiceTest.java              # 单元测试
```

---

## 四、核心代码解析

### 4.1 PersonInfo - POJO 定义

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {

    private String name;          // 姓名
    private Integer age;          // 年龄
    private List<String> hobbies; // 爱好列表

}
```

**注意**：
- 必须有无参构造函数（JSON 反序列化需要）
- 使用 `Integer` 而非 `int`（避免 null 问题）
- 可以使用 Lombok 简化代码

### 4.2 ExtractServiceImpl - 核心实现

```java
@Service
@RequiredArgsConstructor
public class ExtractServiceImpl implements ExtractService {

    private final ChatClient chatClient;

    @Override
    public ExtractResponseDTO extractPersonInfo(String text) {
        // 构建提示词
        String prompt = """
            请从以下文本中提取人员信息...
            文本：%s
            """.formatted(text);

        try {
            // 调用大模型，直接获取结构化对象
            // Spring AI 会自动：
            // 1. 根据目标类型生成 JSON Schema
            // 2. 将 Schema 注入到系统提示中
            // 3. 解析模型响应并映射为 POJO
            PersonInfo personInfo = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(PersonInfo.class);  // ⭐ 关键：使用 entity() 方法

            return ExtractResponseDTO.of(personInfo, text);
        } catch (Exception e) {
            throw new StructuredOutputParseException(
                "无法从文本中提取有效的结构化信息", e);
        }
    }
}
```

**关键点**：

| 方法 | 说明 |
|------|------|
| `chatClient.prompt().user(prompt)` | 设置用户提示词 |
| `.call()` | 同步调用模型 |
| `.entity(PersonInfo.class)` | **核心方法**：自动生成 Schema、解析响应为 POJO |

### 4.3 JSON Schema 自动生成

Spring AI 会自动根据 POJO 类型生成 JSON Schema 并注入到请求中：

```json
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string",
      "description": "姓名"
    },
    "age": {
      "type": "integer",
      "description": "年龄"
    },
    "hobbies": {
      "type": "array",
      "items": {
        "type": "string"
      },
      "description": "爱好列表"
    }
  },
  "required": ["name", "age", "hobbies"]
}
```

### 4.4 异常处理

当模型返回的内容无法解析为目标类型时，`entity()` 方法会抛出异常：

```java
try {
    PersonInfo personInfo = chatClient.prompt()
            .user(prompt)
            .call()
            .entity(PersonInfo.class);
} catch (Exception e) {
    // 捕获异常，返回友好提示
    throw new StructuredOutputParseException(
        "无法从文本中提取有效的结构化信息", e);
}
```

---

## 五、API 接口说明

### 5.1 提取人员信息接口

**请求**：
```http
POST /lab03/extract
Content-Type: application/json

{
  "text": "张三，28岁，爱好是编程和打篮球"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "personInfo": {
      "name": "张三",
      "age": 28,
      "hobbies": ["编程", "打篮球"]
    },
    "originalText": "张三，28岁，爱好是编程和打篮球",
    "timestamp": 1710000000000
  }
}
```

**错误响应**（当无法解析时）：
```json
{
  "code": 500,
  "message": "无法从文本中提取有效的结构化信息，请检查输入内容",
  "data": null
}
```

---

## 六、运行方法

### 6.1 环境准备

| 软件 | 版本要求 |
|------|----------|
| JDK | 21+ |
| Maven | 3.9+ |

### 6.2 配置 API Key

```bash
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://api.siliconflow.cn
```

### 6.3 启动应用

```bash
cd lab03-structured-output
mvn spring-boot:run
```

启动成功后，应用运行在 `http://localhost:8083`。

### 6.4 测试接口

```bash
# 正常请求
curl -X POST http://localhost:8083/lab03/extract \
  -H "Content-Type: application/json" \
  -d '{"text": "李四，30岁，喜欢读书和旅行"}'

# 测试无法提取的情况
curl -X POST http://localhost:8083/lab03/extract \
  -H "Content-Type: application/json" \
  -d '{"text": "今天天气真好"}'
```

---

## 七、单元测试解读

### 7.1 测试策略

```
┌─────────────────────────────────────────────────────┐
│                ExtractServiceTest                    │
├─────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐          │
│  │  正常场景测试     │  │  异常场景测试     │          │
│  │  - entity 解析   │  │  - 解析失败      │          │
│  │  - 单个爱好      │  │  - 返回 null     │          │
│  │  - Prompt 包含文本│  │  - 异常包装      │          │
│  └─────────────────┘  └─────────────────┘          │
└─────────────────────────────────────────────────────┘
```

### 7.2 核心测试用例

```java
@Test
@DisplayName("应正确解析模型返回的结构化对象")
void shouldParseEntity_whenModelReturnsValidData() {
    // given
    PersonInfo expectedPerson = new PersonInfo("张三", 28, List.of("编程", "打篮球"));

    // Mock entity() 方法返回预定义的对象
    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.user(anyString())).thenReturn(requestSpec);
    when(requestSpec.call()).thenReturn(callResponseSpec);
    when(callResponseSpec.entity(PersonInfo.class)).thenReturn(expectedPerson);

    // when
    ExtractResponseDTO response = extractService.extractPersonInfo("测试");

    // then
    assertEquals("张三", response.personInfo().getName());
    assertEquals(28, response.personInfo().getAge());
}

@Test
@DisplayName("模型无法解析时应抛出 StructuredOutputParseException")
void shouldThrowException_whenModelCannotParse() {
    // given
    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.user(anyString())).thenReturn(requestSpec);
    when(requestSpec.call()).thenReturn(callResponseSpec);
    when(callResponseSpec.entity(PersonInfo.class))
            .thenThrow(new RuntimeException("Failed to parse"));

    // when & then
    assertThrows(
        StructuredOutputParseException.class,
        () -> extractService.extractPersonInfo("测试")
    );
}
```

---

## 八、最佳实践

### 8.1 POJO 设计原则

| 原则 | 说明 |
|------|------|
| 使用包装类型 | `Integer` 而非 `int`，避免 null 问题 |
| 添加无参构造 | JSON 反序列化需要 |
| 字段命名规范 | 使用驼峰命名，与 JSON 保持一致 |
| 添加描述注解 | 可使用 `@JsonPropertyDescription` 增强 Schema |

### 8.2 错误处理策略

```java
// ✅ 推荐：捕获异常并返回友好提示
try {
    PersonInfo info = chatClient.prompt()
            .user(prompt)
            .call()
            .entity(PersonInfo.class);
} catch (Exception e) {
    throw new BusinessException("无法解析结构化信息", e);
}

// ❌ 不推荐：直接让异常传播
PersonInfo info = chatClient.prompt().user(prompt).call().entity(PersonInfo.class);
```

### 8.3 温度参数设置

结构化输出场景建议设置较低的温度（0.1-0.3），以获得更确定的输出：

```yaml
spring:
  ai:
    openai:
      chat:
        options:
          temperature: 0.3  # 结构化输出用低温度
```

---

## 九、常见问题

### Q1: 模型返回了 markdown 代码块怎么办？

部分模型会返回带 ` ```json ` 标记的响应。Spring AI 的 `BeanOutputConverter` 会自动处理这种情况。

### Q2: 如何支持更复杂的嵌套结构？

定义嵌套的 POJO 类即可：

```java
@Data
public class OrderInfo {
    private String orderId;
    private CustomerInfo customer;  // 嵌套对象
    private List<OrderItem> items;  // 嵌套列表
}
```

### Q3: 如何提高解析成功率？

1. 在 Prompt 中明确说明输出格式
2. 使用低温度参数
3. 选择支持结构化输出的模型
4. 添加示例（Few-shot Prompting）

---

## 十、下一步

完成本 Lab 后，建议继续学习：

- **Lab04 - 对话记忆**：实现多轮对话的上下文管理
- **Lab05 - 工具调用**：让 AI 调用外部工具/API
