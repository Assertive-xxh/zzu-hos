# 迁移与部署指南（ZZU-hospital-assist）

本文关注“把项目从本机迁到服务器/容器环境，能稳定跑起来”。

## 0. 运行时依赖总览

后端服务依赖以下外部组件（建议生产使用托管版或独立容器）：

- MongoDB：聊天记忆持久化（`spring.data.mongodb.uri`）
- Redis：Session（Spring Session Redis）+ 高并发演示库存/锁
- MySQL：业务数据（MyBatis-Plus）
- Kafka：高并发演示使用 `appointment-topic`
- DashScope（阿里百炼）：Qwen chat / streaming chat / embedding
- Pinecone：向量检索（RAG）

## 1. 必需配置（强烈建议全部 env 化）

当前 `application.yml` 中：
- DashScope 使用 `${ALI_API_KEY}`（已 env 化）
- Pinecone API Key 在代码中读取 `PINECONE_API_KEY`（env）
- MySQL/Mongo/Redis/Kafka 仍是本地默认值（需迁移）

### 1.1 推荐环境变量清单

最低可运行（聊天 + RAG + Mongo 记忆）：
- `ALI_API_KEY`：DashScope API Key
- `PINECONE_API_KEY`：Pinecone API Key
- `SPRING_DATA_MONGODB_URI`

如果需要完整业务（预约落库、会话、并发演示）：
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST` / `SPRING_DATA_REDIS_PORT`（如有密码再加 `SPRING_DATA_REDIS_PASSWORD`）
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`

建议：
- `SPRING_PROFILES_ACTIVE=prod`
- `TZ=Asia/Shanghai`

### 1.2 端口

- 后端：`8080`
- 前端：开发期 Vite 默认 `5173`；生产建议静态站点（Nginx）

## 2. 迁移步骤（服务器/虚拟机）

### 2.1 构建后端

在项目根目录（含 `pom.xml`）构建：

```pwsh
mvn -DskipTests package
```

产物一般在 `target/` 下（Spring Boot jar 的形态取决于你的打包配置）。

### 2.2 启动前检查顺序

建议按以下顺序确保可达：

1. MongoDB 可连接（并且允许创建索引/写入）
2. Redis 可连接（如启用 session）
3. MySQL 可连接（如启用预约 Tools 落库）
4. Kafka 可连接（如启用高并发演示）
5. 外部 SaaS：DashScope、Pinecone（需要出网）

### 2.3 启动命令（示例）

```pwsh
$env:ALI_API_KEY = "<your-ali-api-key>"
$env:PINECONE_API_KEY = "<your-pinecone-api-key>"
$env:SPRING_DATA_MONGODB_URI = "mongodb://<host>:27017/langchain4j_chat_memory"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://<host>:3306/zzuass?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false"
$env:SPRING_DATASOURCE_USERNAME = "<user>"
$env:SPRING_DATASOURCE_PASSWORD = "<password>"
$env:SPRING_DATA_REDIS_HOST = "<redis-host>"
$env:SPRING_DATA_REDIS_PORT = "6379"
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "<kafka-host>:9092"

java -jar .\target\ZZU-hospital-assist-*.jar
```

> 说明：`SPRING_*` 环境变量会覆盖 `application.yml` 默认值。

### 2.4 前端部署

开发期：Vite proxy 把 `/api` 转发到 `http://localhost:8080` 并 rewrite 去掉 `/api`。

生产建议：
- `npm run build` 产物交给 Nginx/静态站点托管
- Nginx 将 `/api` 反代到后端，并 rewrite（或直接让前端请求不带 `/api`）

## 3. Docker Compose（推荐的迁移落地方式）

建议使用 Compose 管理 Mongo/Redis/MySQL/Kafka（仅开发/测试），后端容器通过 env 注入配置。

你需要补齐/新增：
- `docker-compose.yml`
- `.env.example`（不放真实密钥）

（如果你希望我直接在仓库里生成 Compose 与 Nginx 配置，我可以继续实现。）

## 4. 数据迁移与备份

- MongoDB（聊天记忆）
  - 迁移：`mongodump/mongorestore`
  - 关注：索引与 collection 名称一致性、唯一约束
- MySQL（业务数据）
  - 迁移：`mysqldump` 或逻辑迁移工具
  - 建议：引入 Flyway/Liquibase 做 schema 版本管理
- Redis（session/库存）
  - Session 一般不需要迁移；库存如果是生产业务数据，需要明确重建/恢复策略
- Pinecone
  - 记录 index/namespace、embedding 模型与维度；更换 embedding 模型通常需要新建 index

## 5. 生产化建议（部署层面）

- 出网：DashScope/Pinecone 需要稳定外网访问
- 健康检查：`/actuator/health`（已启用）
- 日志：接入集中式日志（ELK/EFK），并统一 traceId
- 安全：密钥放 Secret（K8s Secret/Vault），避免 yml 明文
- 限流与超时：流式接口连接数高，建议网关/Nginx 级别设置超时与最大连接数
