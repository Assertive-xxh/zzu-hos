# ZZU Hospital Assist

郑州大学校医院智能助手后端服务，基于 Spring Boot 3 与 LangChain4j 构建，面向校医院咨询、知识库问答、预约挂号和高并发预约演示等场景。

## 项目能力

- 智能问答：通过 LangChain4j AiService 接入 DashScope Qwen 流式对话模型。
- RAG 检索增强：使用 DashScope `text-embedding-v3` 生成向量，并通过 Pinecone 检索校医院知识内容。
- 对话记忆：使用 MongoDB 持久化聊天历史，支持按 `memoryId` 维护多轮上下文。
- 预约工具调用：LLM 可调用预约相关 Tools，完成号源查询、预约挂号、取消预约等业务动作。
- 高并发演示：提供 Redis、Redisson 分布式锁与 Kafka 异步处理链路，用于模拟预约削峰与并发控制。
- 运维基础：集成 Redis Session、Actuator 健康检查、Knife4j/OpenAPI 文档能力。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 3.2.6, Java 17 |
| AI 编排 | LangChain4j 1.0.0-beta3 |
| 大模型/Embedding | DashScope Qwen, `text-embedding-v3` |
| 向量库 | Pinecone |
| 业务数据 | MySQL, MyBatis-Plus |
| 对话记忆 | MongoDB |
| 会话/锁 | Redis, Spring Session Redis, Redisson |
| 异步消息 | Kafka |
| API 文档 | Knife4j / OpenAPI |

## 目录结构

```text
.
├── docs/                         # 架构、部署、风险说明
├── src/main/java/edu/zzu/...      # 后端源码
│   ├── assistant/                 # LangChain4j AiService
│   ├── config/                    # 模型、RAG、存储等配置
│   ├── controller/                # HTTP 接口
│   ├── tools/                     # LLM 可调用工具
│   ├── service/                   # 业务服务
│   └── listener/                  # Kafka 消费者
├── src/main/resources/            # Spring Boot 配置
├── zzu_ass_ui/frontend/           # 前端项目
└── pom.xml
```

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x
- MongoDB 5.x+
- Redis 6.x+
- Kafka 3.x+（仅高并发预约演示需要）
- DashScope API Key
- Pinecone API Key

## 快速启动

1. 准备依赖服务

   本地默认配置会连接：

   - MySQL：`localhost:3306/zzuass`
   - MongoDB：`localhost:27017/langchain4j_chat_memory`
   - Redis：`localhost:6379`
   - Kafka：`localhost:9092`

2. 设置必要环境变量

   PowerShell 示例：

   ```powershell
   $env:ALI_API_KEY = "<your-dashscope-api-key>"
   $env:PINECONE_API_KEY = "<your-pinecone-api-key>"
   ```

3. 启动后端

   ```bash
   mvn spring-boot:run
   ```

   服务默认监听：

   ```text
   http://localhost:8080
   ```

4. 构建后端

   ```bash
   mvn -DskipTests package
   ```

## 主要接口

| 接口 | 方法 | 说明 |
| --- | --- | --- |
| `/xiaozheng/chat` | POST | 小郑助手流式对话，返回 `text/stream` |
| `/api/auth/login` | POST | 演示登录，写入 Session |
| `/api/auth/logout` | POST | 注销 Session |
| `/api/appointment/make` | POST | 高并发预约演示入口 |
| `/actuator/health` | GET | 服务健康检查 |

聊天接口请求示例：

```json
{
  "memoryId": "user-001",
  "message": "我想预约明天上午的内科"
}
```

## 配置说明

`src/main/resources/application.yml` 已提供本地开发默认值。生产或服务器部署时，建议使用环境变量覆盖敏感信息和外部服务地址：

| 配置 | 说明 |
| --- | --- |
| `ALI_API_KEY` | DashScope 模型与 Embedding API Key |
| `PINECONE_API_KEY` | Pinecone API Key |
| `SPRING_DATA_MONGODB_URI` | MongoDB 连接地址 |
| `SPRING_DATASOURCE_URL` | MySQL JDBC 地址 |
| `SPRING_DATASOURCE_USERNAME` | MySQL 用户名 |
| `SPRING_DATASOURCE_PASSWORD` | MySQL 密码 |
| `SPRING_DATA_REDIS_HOST` | Redis 主机 |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka 地址 |

不要把真实密钥提交到仓库。

## 项目文档

- [核心架构](docs/architecture.md)
- [迁移与部署指南](docs/deployment.md)
- [缺口与风险清单](docs/gaps-and-risks.md)

## 开发约定

- 推荐从 `dev` 分支开发。
- 提交前优先运行 `mvn test` 或至少执行 `mvn -DskipTests package`。
- IDE 工作区文件如 `.idea/workspace.xml` 不建议提交。
- 新增配置请优先提供可覆盖的环境变量，避免硬编码密钥、主机和密码。

## License

当前仓库尚未声明开源许可证。如需公开复用，请先补充明确的 LICENSE 文件。
