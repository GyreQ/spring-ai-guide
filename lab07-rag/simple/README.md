# Lab07 - RAG Simple VectorStore

本模块演示使用 `SimpleVectorStore` 实现 RAG（Retrieval Augmented Generation，检索增强生成）。

## SimpleVectorStore 特点

`SimpleVectorStore` 是 Spring AI 提供的最简单的向量存储实现：

| 特性 | 说明 |
|------|------|
| **存储方式** | 基于内存存储 |
| **持久化** | 支持保存到本地文件（需手动调用） |
| **适用场景** | 教育学习、开发测试、原型验证 |
| **生产环境** | ❌ 不推荐，建议使用专业的向量数据库 |

### 重要提醒

> **⚠️ 重启后数据丢失**
>
> SimpleVectorStore 默认将数据存储在内存中，应用重启后数据会丢失。
> 如需持久化，可调用 `simpleVectorStore.saveToFile()` 方法。

## 技术架构

### ETL Pipeline

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Extract       │     │   Transform     │     │   Load          │
│   TikaDocument  │ ──► │   TokenText     │ ──► │   VectorStore   │
│   Reader        │     │   Splitter      │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

1. **Extract（提取）**: 使用 `TikaDocumentReader` 解析文档（支持 PDF、DOC、TXT 等）
2. **Transform（转换）**: 使用 `TokenTextSplitter` 将长文本分割成小块
3. **Load（加载）**: 将文本块存入 `VectorStore`

### RAG 问答流程

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   用户问题      │     │   向量检索      │     │   上下文注入    │
│   Question      │ ──► │   Similarity    │ ──► │   Context       │
│                 │     │   Search        │     │   Injection     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                                                        ▼
                                                ┌─────────────────┐
                                                │   LLM 生成回答  │
                                                │   Generation    │
                                                └─────────────────┘
```

## 实现方式：QuestionAnswerAdvisor

本项目采用 **Advisor 模式** 实现 RAG，这是 Spring AI 推荐的标准写法。

### 配置方式

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore) {
    return builder
            .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
            .build();
}
```

### 工作原理

`QuestionAnswerAdvisor` 自动完成以下步骤：

1. **拦截用户请求**: 当用户发送问题时，Advisor 会拦截请求
2. **向量检索**: 使用用户问题在 VectorStore 中进行相似度搜索
3. **上下文注入**: 将检索到的相关文档作为上下文添加到 Prompt 中
4. **模型调用**: 将增强后的 Prompt 发送给 LLM 生成回答

### Advisor vs 手动检索对比

| 特性 | QuestionAnswerAdvisor | 手动检索 |
|------|----------------------|----------|
| **代码量** | 少（配置即可） | 多（需手动实现） |
| **灵活性** | 较低 | 高 |
| **适用场景** | 标准 RAG 场景 | 需要自定义检索逻辑 |
| **维护成本** | 低 | 高 |

### 适用场景

**推荐使用 Advisor 模式：**
- 标准 RAG 问答场景
- 快速原型开发
- 不需要复杂的检索逻辑

**推荐使用手动检索：**
- 需要自定义相似度阈值
- 需要对检索结果进行后处理
- 需要结合多个检索源

## API 接口

### 1. 上传文档

```http
POST /lab07/simple/upload
Content-Type: multipart/form-data

file: <文档文件>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "chunkCount": 15,
    "fileName": "document.pdf"
  }
}
```

### 2. 问答

```http
POST /lab07/simple/chat
Content-Type: application/json

{
  "question": "文档中提到了哪些内容？"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "根据文档内容，...",
    "model": "Qwen/Qwen3-8B"
  }
}
```

## 依赖说明

```xml
<!-- 简单的内存向量库 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-simple</artifactId>
</dependency>

<!-- 文档解析器（支持 PDF、DOC、PPT 等格式） -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-tika-document-reader</artifactId>
</dependency>
```

## 运行方式

```bash
# 设置环境变量
export OPENAI_API_KEY=your-api-key

# 运行应用
mvn spring-boot:run

# 服务端口
# http://localhost:8087
```

## 测试

```bash
# 上传文档
curl -X POST http://localhost:8087/lab07/simple/upload \
  -F "file=@test-document.pdf"

# 问答
curl -X POST http://localhost:8087/lab07/simple/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "文档的主要内容是什么？"}'
```

## 参考资料

- [Spring AI ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)
- [Spring AI Vector Store](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [Spring AI ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
