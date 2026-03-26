# Lab07 - RAG PgVector

本模块演示使用 PostgreSQL 的 `pgvector` 扩展实现 RAG（Retrieval Augmented Generation，检索增强生成）。

## PgVector 与 SimpleVectorStore 对比

| 特性 | SimpleVectorStore | PgVector |
|------|-------------------|----------|
| **存储方式** | 内存存储 | PostgreSQL 数据库 |
| **持久化** | 需手动保存到文件 | 自动持久化 |
| **重启后数据** | ❌ 丢失 | ✅ 保留 |
| **适用场景** | 教育学习、原型验证 | 生产环境 |
| **扩展性** | 单机 | 支持集群部署 |
| **查询性能** | 一般 | 支持索引优化（HNSW、IVFFlat） |

## 快速开始

### 1. 启动 PostgreSQL（带 pgvector 扩展）

```bash
cd lab07-rag/pg
docker-compose up -d
```

等待 PostgreSQL 启动完成后，可以使用以下命令验证：

```bash
# 查看容器状态
docker ps

# 进入 PostgreSQL 验证 pgvector 扩展
docker exec -it spring-ai-pgvector psql -U postgres -d vector_db -c "SELECT * FROM pg_extension WHERE extname = 'vector';"
```

### 2. 运行应用

```bash
# 设置环境变量
export OPENAI_API_KEY=your-api-key

# 运行应用
mvn spring-boot:run

# 服务端口
# http://localhost:8088
```

## 配置说明

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vector_db
    username: postgres
    password: postgres

  ai:
    vectorstore:
      pgvector:
        initialize-schema: true
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
```

### 核心参数说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `initialize-schema` | 是否自动创建 vector_store 表和 pgvector 扩展 | `false` |
| `index-type` | 向量索引类型：`NONE`、`IVFFlat`、`HNSW` | `HNSW` |
| `distance-type` | 距离计算方式：`COSINE_DISTANCE`、`EUCLIDEAN_DISTANCE`、`NEGATIVE_INNER_PRODUCT` | `COSINE_DISTANCE` |
| `dimensions` | 向量维度（需与 EmbeddingModel 匹配） | 自动从 EmbeddingModel 获取 |

### 索引类型说明

- **NONE**: 不创建索引，适合小数据量或测试场景
- **IVFFlat**: 倒排文件索引，适合中大数据量，查询速度较快
- **HNSW**: 层次导航小世界图索引，适合大规模数据，查询速度最快（推荐）

### 距离类型说明

- **COSINE_DISTANCE**: 余弦距离，衡量向量方向的相似性（推荐）
- **EUCLIDEAN_DISTANCE**: 欧几里得距离，衡量向量空间中的直线距离
- **NEGATIVE_INNER_PRODUCT**: 负内积，适合某些特定场景

## Docker Compose 配置

```yaml
version: '3.8'

services:
  postgres:
    image: pgvector/pgvector:pg16
    container_name: spring-ai-pgvector
    environment:
      POSTGRES_DB: vector_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pgvector_data:/var/lib/postgresql/data

volumes:
  pgvector_data:
```

## API 接口

### 1. 上传文档

```http
POST /lab07/pg/upload
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
POST /lab07/pg/chat
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
curl -X POST http://localhost:8088/lab07/pg/upload \
  -F "file=@test-document.pdf"

# 问答
curl -X POST http://localhost:8088/lab07/pg/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "文档的主要内容是什么？"}'
```

## 与 SimpleVectorStore 的代码差异

切换向量库实现时，业务代码几乎不变：

| 变更点 | SimpleVectorStore | PgVector |
|--------|-------------------|----------|
| **依赖** | `spring-ai-advisors-vector-store` | `spring-ai-starter-vector-store-pgvector` |
| **VectorStore Bean** | 需手动创建 | 自动配置 |
| **数据源配置** | 无需配置 | 需配置 PostgreSQL |
| **业务代码** | 完全相同 | 完全相同 |

## 数据库表结构

启用 `initialize-schema: true` 后，Spring AI 会自动创建以下表结构：

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(1536)
);

CREATE INDEX ON vector_store USING hnsw (embedding vector_cosine_ops);
```

## 参考资料

- [Spring AI PGvector](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html)
- [pgvector GitHub](https://github.com/pgvector/pgvector)
- [Spring AI ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)
