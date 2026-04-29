# 缺失项与风险点（建议补充清单）

本文件把“能跑”与“能长期稳定运行”之间的差距列出来，并给出可执行补齐建议。

## 1. Mongo 聊天记忆：索引设计可能与多记忆类型冲突

现状：

- `MongoChatMemoryStore` 查询条件包含 `memoryId + memoryType`（SHORT/LONG）
- `MongoIndexInitializer`（启动时）为集合（推断 `chat_messages`）对 `memoryId` 建唯一索引（若仅单列唯一）

风险：

- 一旦启用 SHORT/LONG 两条记录，同一 `memoryId` 会违反唯一约束，导致 upsert/写入异常。

建议：

- 将唯一索引调整为**复合唯一**：`memoryId + memoryType`
- 明确并统一 collection 名称：`MongoTemplate` 默认的 collection 推断与 initializer 使用的 collection 字符串要一致

## 2. 高并发预约链路：Kafka 消费端可能缺失

现状：

- `HighConcurrencyController/Service` 发送 `appointment-topic`
- 未确认是否存在 `@KafkaListener` 消费者落库/幂等处理

风险：

- 生产环境会出现“已扣库存但未落库”的不一致

建议：

- 增加 Kafka consumer：幂等键（业务唯一号）+ 重试 + DLQ
- 增加事务边界与补偿：扣减库存与落库之间的失败补偿策略

## 3. DB Schema 管理缺失（Flyway/Liquibase）

现状：

- `application.yml` 指向 `zzuass`，但仓库未提供迁移脚本

风险：

- 新环境无法自动初始化表结构；升级无法回滚

建议：

- 引入 Flyway 或 Liquibase
- 为核心表（预约记录等）提供 versioned migration

## 4. 配置与密钥管理（prod 必须改）

现状：

- MySQL 用户名密码在 `application.yml` 明文（root/root，明显仅本地）
- `ALI_API_KEY` 采用 env（正确）
- `PINECONE_API_KEY` 从 env 取，但硬编码在 `EmbeddingStoreConfig` 里读取 `System.getenv()`（不利于统一配置管理）

建议：

- 将所有连接信息/密钥 env 化，并区分 profile（dev/test/prod）
- 优先使用 Spring 配置体系（`@ConfigurationProperties`），避免散落 `System.getenv()`

## 5. CORS / 鉴权策略需明确

现状：

- 前端 dev 用 Vite proxy；生产分离部署时需要 CORS 或统一网关
- 拦截器默认拦截所有请求，仅放行登录/错误（需确认 `/xiaozheng/chat` 是否应该放行）

建议：

- 明确鉴权方案：Session / JWT / OAuth2
- 明确哪些接口需要登录，哪些可匿名
- 增加 CORS 配置（或网关统一处理）

## 6. 初始化逻辑生产化

现状：

- `DataInitializer` 会在启动时写入模拟库存 key（生产可能覆盖真实库存）
- Pinecone index 可能在启动时自动创建（需要权限与网络稳定）

建议：

- 初始化数据拆为一次性 job 或仅 dev profile 生效
- 对 Pinecone：把 index/namespace/维度作为基础设施契约记录，并提供“检测/告警而非强行创建”的生产策略

## 7. 可观测性与稳定性

建议补齐：

- 结构化日志（traceId/spanId）+ OpenTelemetry（可选）
- 指标：LLM 调用耗时/错误率、Pinecone 检索耗时、Mongo/Redis/Kafka/MySQL 连接与超时
- 超时与限流：流式接口连接数高，建议网关/Nginx 限制并设合理超时
- 备份与容灾：Mongo/MySQL/Redis 的备份与恢复演练
