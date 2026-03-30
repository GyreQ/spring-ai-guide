# Lab07 - RAG Milvus

本模块演示使用 Milvus 向量数据库实现 RAG（Retrieval Augmented Generation，检索增强生成）。

## Milvus 与其他向量库对比

| 特性 | SimpleVectorStore | PgVector | Milvus |
|------|-------------------|----------|--------|
| **存储方式** | 内存存储 | PostgreSQL 扩展 | 独立向量数据库 |
| **持久化** | 需手动保存到文件 | 自动持久化 | 自动持久化 |
| **重启后数据** | ❌ 丢失 | ✅ 保留 | ✅ 保留 |
| **适用场景** | 教育学习、原型验证 | 中小规模生产环境 | 大规模生产环境 |
| **扩展性** | 单机 | 支持集群部署 | 原生分布式架构 |
| **索引类型** | 无 | HNSW、IVFFlat | IVF_FLAT、IVF_SQ8、HNSW 等 |
| **相似度度量** | 余弦相似度 | 余弦、欧几里得、内积 | 余弦、欧几里得、内积 |
| **部署复杂度** | 无需部署 | 需部署 PostgreSQL | 需单独部署 Milvus |

## 快速开始

### 1. 启动 Milvus（Docker 方式）

```bash
cd lab07-rag/milvus

# 下载官方 Docker Compose 配置
wget https://github.com/milvus-io/milvus/releases/download/v2.6.13/milvus-standalone-docker-compose.yml -O docker-compose.yml

# 启动 Milvus
docker compose up -d
```

等待 Milvus 启动完成后，可以使用以下命令验证：

```bash
# 查看容器状态
docker ps | grep milvus

# 检查 Milvus 健康状态
curl http://localhost:9091/healthz
```

**端口说明**：
- `19530`: Milvus 服务端口（gRPC）
- `9091`: Milvus 健康检查端口
- `8080`: Milvus Web UI（Attu，可选）

### 2. 运行应用

```bash
# 设置环境变量
export OPENAI_API_KEY=your-api-key

# 运行应用
mvn spring-boot:run

# 服务端口
# http://localhost:8089
```

## 配置说明

### application.yml

```yaml
spring:
  ai:
    vectorstore:
      milvus:
        client:
          host: ${MILVUS_HOST:localhost}
          port: ${MILVUS_PORT:19530}
        database-name: default
        collection-name: lab07_rag_milvus
        embedding-dimension: 1536
        index-type: IVF_FLAT
        metric-type: COSINE
        initialize-schema: true
```

### 核心参数说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `client.host` | Milvus 服务地址 | `localhost` |
| `client.port` | Milvus 服务端口 | `19530` |
| `client.username` | 用户名（如需认证） | - |
| `client.password` | 密码（如需认证） | - |
| `database-name` | 数据库名称 | `default` |
| `collection-name` | 集合名称 | 自动生成 |
| `embedding-dimension` | 向量维度（需与 EmbeddingModel 匹配） | 自动从 EmbeddingModel 获取 |
| `index-type` | 索引类型 | `IVF_FLAT` |
| `metric-type` | 相似度度量类型 | `COSINE` |
| `initialize-schema` | 是否自动创建 Collection 和索引 | `false` |

### 索引类型说明

- **IVF_FLAT**: 倒排文件索引，平衡查询速度和精度（推荐）
- **IVF_SQ8**: 量化索引，节省存储空间
- **HNSW**: 层次导航小世界图索引，查询速度最快
- **AUTOINDEX**: 自动选择索引类型

### 相似度度量说明

- **COSINE**: 余弦相似度，衡量向量方向的相似性（推荐）
- **EUCLIDEAN**: 欧几里得距离，衡量向量空间中的直线距离
- **INNER_PRODUCT**: 内积，适合归一化向量

## API 接口

### 1. 上传文档

```http
POST /lab07/milvus/upload
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
POST /lab07/milvus/chat
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

## 测试

```bash
# 上传文档
curl -X POST http://localhost:8089/lab07/milvus/upload \
  -F "file=@test-document.pdf"

# 问答
curl -X POST http://localhost:8089/lab07/milvus/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "文档的主要内容是什么？"}'
```

## 与其他向量库的代码差异

切换向量库实现时，业务代码几乎不变：

| 变更点 | SimpleVectorStore | PgVector | Milvus |
|--------|-------------------|----------|--------|
| **依赖** | `spring-ai-advisors-vector-store` | `spring-ai-starter-vector-store-pgvector` | `spring-ai-starter-vector-store-milvus` |
| **VectorStore Bean** | 需手动创建 | 自动配置 | 自动配置 |
| **数据源配置** | 无需配置 | 需配置 PostgreSQL | 需配置 Milvus 连接 |
| **业务代码** | 完全相同 | 完全相同 | 完全相同 |

## Milvus 数据结构

启用 `initialize-schema: true` 后，Spring AI 会自动创建以下数据结构：

```python
# Collection 结构
collection_name: lab07_rag_milvus
fields:
  - id (VARCHAR, 主键)
  - content (VARCHAR, 文本内容)
  - metadata (JSON, 元数据)
  - embedding (FLOAT_VECTOR, 1536维)

# 索引
index_type: IVF_FLAT
metric_type: COSINE
```

## Milvus 可视化工具

推荐使用 **Attu**（Milvus 官方可视化管理工具）：

```bash
# 启动 Attu
docker run -d --name attu \
  -p 3000:3000 \
  -e MILVUS_URL=host.docker.internal:19530 \
  zilliz/attu:latest

# 访问 http://localhost:3000
```

## 参考资料

- [Spring AI Milvus](https://docs.spring.io/spring-ai/reference/api/vectordbs/milvus.html)
- [Milvus 官方文档](https://milvus.io/docs/)
- [Milvus GitHub](https://github.com/milvus-io/milvus)
- [Attu 可视化工具](https://github.com/zilliztech/attu)
