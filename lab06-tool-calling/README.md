# Lab06 - Tool Calling（工具调用）

> 让 AI 模型调用外部工具，突破模型能力边界

## 一、核心概念

### 1.1 什么是 Tool Calling？

Tool Calling（工具调用）是指 AI 模型在生成回复时，能够主动请求调用外部工具来获取信息或执行操作。这使得模型可以：

- 获取实时数据（如天气、股票价格）
- 执行计算操作（如数学运算、数据转换）
- 访问私有数据源（如企业数据库）
- 触发外部系统动作（如发送邮件、创建订单）

### 1.2 Tool Calling 执行流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Tool Calling 执行流程                         │
└─────────────────────────────────────────────────────────────────────┘

    用户问题                模型判断                工具调用
       │                      │                      │
       ▼                      ▼                      ▼
  ┌─────────┐           ┌─────────┐           ┌─────────────┐
  │ "北京今 │    ───►   │ 模型分析│    ───►   │ 决定调用    │
  │ 天天气？│           │ 问题意图│           │ getWeather  │
  └─────────┘           └─────────┘           └─────────────┘
                                                     │
                                                     ▼
    最终回复              整合结果              执行工具
       │                      │                      │
       ▼                      ▼                      ▼
  ┌─────────┐           ┌─────────┐           ┌─────────────┐
  │ "北京今 │    ◄───   │ 模型生成│    ◄───   │ getWeather  │
  │ 天28℃晴"│           │ 最终回复│           │ ("北京")    │
  └─────────┘           └─────────┘           └─────────────┘
                                                     │
                                                     ▼
                                              ┌─────────────┐
                                              │ 返回：      │
                                              │ "28℃, 晴朗" │
                                              └─────────────┘

执行流程详解：
1. 用户发送问题 → 模型分析问题意图
2. 模型判断需要调用工具 → 返回 Tool Call Request
3. 框架执行对应工具方法 → 获取 Tool Result
4. 将工具结果返回模型 → 模型生成最终回复
```

### 1.3 @Tool 注解的作用

Spring AI 的 `@Tool` 注解是定义工具方法的最佳方式：

```java
@Tool(description = "获取指定城市的当前天气信息")
public String getWeather(@ToolParam(description = "城市名称") String city) {
    // 工具实现
}
```

**Spring AI 自动完成的工作**：

| 步骤 | 说明 |
|------|------|
| 1. JSON Schema 生成 | 根据方法签名自动生成工具描述 Schema |
| 2. 参数解析 | 自动解析模型传入的 JSON 参数 |
| 3. 方法调用 | 使用反射调用对应的工具方法 |
| 4. 结果序列化 | 将方法返回值序列化为 JSON 返回给模型 |

## 二、核心实现

### 2.1 定义工具类

```java
@Component
public class WeatherTools {

    @Tool(description = "获取指定城市的当前天气信息")
    public String getWeather(
            @ToolParam(description = "城市名称，如：北京、上海") String city) {

        // 模拟调用第三方天气 API
        return "城市: " + city + ", 温度: 25℃, 天气: 晴朗";
    }

    @Tool(description = "获取未来几天的天气预报")
    public String getWeatherForecast(
            @ToolParam(description = "城市名称") String city,
            @ToolParam(description = "预报天数，1-7天") Integer days) {

        // 返回天气预报
        return forecastInfo;
    }
}
```

### 2.2 注册工具到 ChatClient

```java
@Configuration
@RequiredArgsConstructor
public class ToolCallingChatClientConfig {

    private final WeatherTools weatherTools;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultTools(weatherTools)  // 注册默认工具
                .build();
    }
}
```

### 2.3 业务调用

```java
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final ChatClient chatClient;

    @Override
    public WeatherResponseDTO chat(String question) {
        // 框架自动处理工具调用，无需额外代码
        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();

        return WeatherResponseDTO.of(question, answer);
    }
}
```

## 三、进阶功能

### 3.1 工具执行异常处理

当工具执行抛出异常时，Spring AI 提供两种处理方式：

**配置文件方式**：
```yaml
spring:
  ai:
    tools:
      # false: 将异常信息发送给模型，让模型自我纠正
      # true: 直接抛出异常给调用者
      throw-exception-on-error: false
```

**工具中的异常处理**：
```java
@Tool(description = "获取天气信息")
public String getWeather(String city) {
    if ("火星".equals(city)) {
        // 抛出异常，模型会收到错误信息并生成友好的回复
        throw new IllegalArgumentException("无法查询火星的天气信息");
    }
    return weatherInfo;
}
```

**模型自我纠正示例**：
- 用户问："火星的天气怎么样？"
- 工具抛出异常："无法查询火星的天气信息"
- 模型回复："抱歉，我无法查询火星的天气信息，请提供一个地球上的城市名称。"

### 3.2 @Tool 注解属性

| 属性 | 说明 | 示例 |
|------|------|------|
| `name` | 工具名称，默认使用方法名 | `@Tool(name = "weather_query")` |
| `description` | 工具描述，帮助模型理解何时调用 | `@Tool(description = "获取天气信息")` |
| `returnDirect` | 是否直接返回结果给调用者 | `@Tool(returnDirect = true)` |

### 3.3 @ToolParam 注解属性

| 属性 | 说明 | 示例 |
|------|------|------|
| `description` | 参数描述 | `@ToolParam(description = "城市名称")` |
| `required` | 是否必需，默认 true | `@ToolParam(required = false)` |

## 四、最佳实践

### 4.1 工具描述的重要性

**好的描述** 能让模型更准确地判断何时调用：

```java
// ✅ 好的描述
@Tool(description = "获取指定城市的当前天气信息，包括温度、天气状况、湿度等")
public String getWeather(String city) { ... }

// ❌ 差的描述
@Tool(description = "查天气")
public String getWeather(String city) { ... }
```

### 4.2 参数校验

在工具方法内部进行必要的参数检查：

```java
@Tool(description = "获取天气预报")
public String getWeatherForecast(String city, Integer days) {
    // 参数校验
    if (days < 1 || days > 7) {
        throw new IllegalArgumentException("预报天数必须在1-7天之间");
    }
    // 业务逻辑
}
```

### 4.3 返回值设计

工具返回值应清晰、结构化：

```java
// ✅ 结构化返回
return String.format("城市: %s, 温度: %d℃, 天气: %s, 湿度: %d%%",
    city, temp, condition, humidity);

// ❌ 过于简略
return temp + "";
```

## 五、@Tool vs FunctionCallback 对比

| 特性 | @Tool 注解 | FunctionCallback |
|------|-----------|-----------------|
| **代码风格** | Spring 原生注解，简洁 | 需要实现接口，繁琐 |
| **Schema 生成** | 自动生成 | 需要手动定义 |
| **参数绑定** | 自动解析绑定 | 需要手动处理 |
| **可读性** | 高，方法即文档 | 较低，逻辑分散 |
| **维护成本** | 低 | 高 |

**推荐**：优先使用 `@Tool` 注解方式，这是 Spring AI 官方推荐的最佳实践。

## 六、测试验证

### 6.1 触发工具调用

```bash
curl "http://localhost:8086/api/v1/weather/chat?question=北京今天天气怎么样？"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "question": "北京今天天气怎么样？",
    "answer": "根据查询结果，北京今天天气晴朗，温度28℃，湿度45%..."
  }
}
```

### 6.2 异常场景测试

```bash
curl "http://localhost:8086/api/v1/weather/chat?question=火星的天气怎么样？"
```

**预期响应**（模型自我纠正）：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "question": "火星的天气怎么样？",
    "answer": "抱歉，我无法查询火星的天气信息，请提供一个地球上的城市名称。"
  }
}
```

## 七、参考资料

- [Spring AI Tool Calling 官方文档](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [OpenAI Function Calling 文档](https://platform.openai.com/docs/guides/function-calling)
