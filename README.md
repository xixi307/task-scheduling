<div>
   <img align="left" src="https://raw.githubusercontent.com/sonus21/rqueue/master/rqueue-core/src/main/resources/public/rqueue/img/android-chrome-192x192.png" alt="Rqueue Logo" width="90">
   <h1 style="float:left">Task Scheduling - 基于 Redis 的分布式任务调度系统</h1>
</div>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)](https://redis.io/)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

**Task Scheduling** 是一个基于 Rqueue 框架构建的高性能分布式任务调度和消息队列系统，专为 Spring 和 Spring Boot 应用设计。它使用 Redis 作为后端存储，提供异步任务执行、定时调度、延迟任务和事件驱动工作流等完整解决方案，内置 Web 管理仪表板和实时监控能力。

<br/>

## 📋 目录

- [核心特性](#核心特性)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [使用示例](#使用示例)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [监控与仪表板](#监控与仪表板)
- [开发指南](#开发指南)
- [常见问题](#常见问题)
- [许可证](#许可证)

## ✨ 核心特性

### 🚀 强大的任务调度
- **异步任务处理**：后台异步执行，不阻塞主线程
- **灵活调度**：支持立即执行、延迟执行、定时执行和周期性执行
- **至少一次交付**：确保任务至少被执行一次，提高可靠性
- **智能重试**：支持固定间隔或指数退避策略，可配置重试次数
- **长任务支持**：通过定期签入（check-in）机制支持长时间运行任务
- **自动序列化**：透明处理消息的序列化和反序列化

### 📬 高级队列管理
- **消息去重**：基于唯一消息 ID 实现精确去重
- **优先级队列**：支持多级优先级（如 critical=10, high=8, medium=4, low=1）
- **队列组优先级**：支持组级别优先级和加权/严格排序策略
- **消息广播**：一条消息可同时分发到多个监听器
- **批量拉取**：支持批量消费提升吞吐量
- **死信队列**：失败任务自动转移到死信队列，便于后续处理

### 👥 灵活的消费者模型
- **注解驱动**：使用 `@RqueueListener` 注解轻松配置监听器
- **零配置启动**：Spring Boot 应用添加依赖即可使用
- **并行消费**：支持多竞争消费者并行处理，提升性能
- **动态并发**：支持固定或动态范围并发控制（如 "5-10"）
- **中间件支持**：可在监听器执行前后添加自定义中间件

### 🔧 完善的运维能力
- **回调机制**：支持任务成功、失败、丢弃等事件的回调通知
- **事件订阅**：可订阅引导完成、任务执行等系统事件
- **实时监控**：跟踪进行中、排队中、已调度的任务状态
- **Web 仪表板**：内置美观的管理界面，可视化队列和任务
- **指标集成**：集成 Micrometer，支持 Prometheus + Grafana 监控
- **日志追踪**：完整的任务执行日志和链路追踪

### 💾 灵活的部署方案
- **独立 Redis 配置**：可为任务调度配置专用 Redis 实例
- **多种部署模式**：支持单机、哨兵和集群模式
- **高性能客户端**：基于 Lettuce 7.x 客户端，性能优异
- **响应式支持**：完美支持 Spring WebFlux 和响应式编程
- **跨平台**：支持 Linux、Windows、macOS 和 Docker 部署

## 🛠️ 技术栈

### 运行环境要求
- **JDK**: 21+
- **Spring Framework**: 7.0.3+
- **Spring Boot**: 4.0.1+（可选，支持传统 Spring）
- **Redis**: 6.0+（单机/哨兵/集群）
- **构建工具**: Gradle 9.3.0+

### 核心依赖
| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Data Redis | 4.0.2 | Redis 数据访问抽象 |
| Lettuce | 7.2.1.RELEASE | 高性能 Redis 客户端 |
| Jackson | 3.0.3 | JSON 序列化/反序列化 |
| Micrometer | 1.16.2 | 指标监控框架 |
| Lombok | 1.18.42 | 代码简化工具 |
| Logback | 1.5.25 | 日志框架 |

### 测试框架
- **JUnit Jupiter**: 5.5.0
- **Mockito**: 3.5.0
- **Embedded Redis**: 1.4.3（嵌入式 Redis 用于测试）

## 🚀 快速开始

### 前置条件

确保已安装：
- JDK 21 或更高版本
- Redis 6.0 或更高版本
- Gradle 9.3.0 或更高版本（或使用项目自带的 gradlew）

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd task-scheduling
```

### 2. 启动 Redis

**方式一：使用 Docker**
```bash
docker run -d --name redis -p 6379:6379 redis:latest
```

**方式二：使用 docker-compose**
```bash
docker-compose up -d redis
```

**方式三：本地安装**
```bash
# macOS
brew install redis
redis-server

# Linux
sudo apt-get install redis-server
sudo systemctl start redis
```

### 3. 构建项目

```bash
# 编译并跳过测试（快速构建）
./gradlew build -x test

# 格式化代码后构建（推荐）
./gradlew formatJava build

# 完整构建（包含测试）
./gradlew clean build
```

### 4. 运行示例应用

**Spring Boot 示例（推荐）**
```bash
./gradlew rqueue-spring-boot-example:bootRun
```

**Spring Boot WebFlux 响应式示例**
```bash
./gradlew rqueue-spring-boot-reactive-example:bootRun
```

**传统 Spring MVC 示例**
```bash
./gradlew rqueue-spring-example:bootRun
```

### 5. 验证运行

访问以下地址确认应用正常运行：
- **应用首页**: http://localhost:8080
- **管理仪表板**: http://localhost:8080/rqueue
- **健康检查**: http://localhost:8080/actuator/health

---

## 📁 项目结构

```
task-scheduling/
├── rqueue-core/                    # 核心模块 - 任务调度引擎
│   ├── annotation/                 # 注解定义（@RqueueListener 等）
│   ├── config/                     # 配置类
│   ├── core/                       # 核心功能实现
│   ├── converter/                  # 消息转换器
│   ├── dao/                        # 数据访问层
│   ├── listener/                   # 监听器实现
│   ├── metrics/                    # 指标监控
│   ├── models/                     # 数据模型
│   ├── utils/                      # 工具类
│   └── web/                        # Web 仪表板
│
├── rqueue-spring/                  # Spring Framework 集成
├── rqueue-spring-boot-starter/     # Spring Boot 自动配置
├── rqueue-spring-common-test/      # 通用测试工具
├── rqueue-test-util/               # 测试工具类
│
├── rqueue-spring-example/          # Spring MVC 示例
├── rqueue-spring-boot-example/     # Spring Boot 示例
└── rqueue-spring-boot-reactive-example/  # Spring Boot WebFlux 示例
```

### 模块说明

| 模块 | 说明 |
|------|------|
| **rqueue-core** | 核心任务调度引擎，包含所有核心功能实现 |
| **rqueue-spring** | Spring Framework 集成模块 |
| **rqueue-spring-boot-starter** | Spring Boot 自动配置 starter |
| **rqueue-spring-common-test** | 通用测试工具和基类 |
| **rqueue-test-util** | 测试辅助工具 |
| **示例模块** | 各种使用场景的示例应用 |

---

## ⚙️ 配置说明

### Spring Boot 自动配置

对于 Spring Boot 应用，只需添加依赖即可自动配置，无需额外配置：

```groovy
// build.gradle
dependencies {
    implementation 'com.github.sonus21:rqueue-spring-boot-starter:4.0.0-RC2'
}
```

### Spring Framework 手动配置

对于非 Spring Boot 应用，需要手动配置：

```java
@Configuration
@EnableRqueue
public class AppConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        return new LettuceConnectionFactory(config);
    }
}
```

### 常用配置项

在 `application.yml` 中配置：

```yaml
# Redis 配置
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0

# Rqueue 配置
rqueue:
  dashboard-enabled: true       # 是否启用仪表板
  dashboard-path: /rqueue       # 仪表板路径
  metrics:
    enabled: true               # 启用指标监控
```

### 高级配置示例

#### 队列优先级配置

```java
// 定义优先级枚举
enum Priority {
    CRITICAL("critical"),
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");
    
    private final String value;
    
    Priority(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

// 使用优先级
@RqueueListener(
    value = "task-queue",
    priority = "critical=10,high=8,medium=4,low=1"
)
public void handleTask(Task task) {
    // 处理任务
}
```

#### 并发控制

```java
// 固定并发数
@RqueueListener(value = "queue-1", concurrency = "5")
public void handleMessage(Message msg) { }

// 动态并发范围（最小5，最大10）
@RqueueListener(value = "queue-2", concurrency = "5-10")
public void handleMessage(Message msg) { }
```

#### 重试与死信队列

```java
@RqueueListener(
    value = "order-queue",
    numRetries = "3",                          // 最多重试3次
    deadLetterQueue = "failed-orders",         // 死信队列
    deadLetterQueueListenerEnabled = "true",   // 启用死信队列监听
    visibilityTimeout = "300000"               // 可见性超时5分钟
)
public void handleOrder(Order order) {
    // 处理订单
}
```

---

## 💡 使用示例

### 1. 添加依赖

**Spring Boot 应用（推荐）**
```groovy
// build.gradle
dependencies {
    implementation 'com.github.sonus21:rqueue-spring-boot-starter:4.0.0-RC2'
}
```

**传统 Spring 应用**
```groovy
dependencies {
    implementation 'com.github.sonus21:rqueue-spring:4.0.0-RC2'
}
```

### 2. 发布任务

使用 `RqueueMessageEnqueuer` Bean 提交任务：

```java
@Service
public class TaskService {

    @Autowired
    private RqueueMessageEnqueuer enqueuer;

    // ✅ 立即执行
    public void sendNotification(String message) {
        enqueuer.enqueue("notification-queue", message);
    }

    // ⏰ 延迟执行（30秒后）
    public void scheduleNotification(String message) {
        enqueuer.enqueueIn("notification-queue", message, 30_000L);
    }

    // 📅 指定时间执行
    public void scheduleAt(Invoice invoice, Instant instant) {
        enqueuer.enqueueAt("invoice-queue", invoice, instant);
    }

    // 🔄 周期性任务（每1分钟执行）
    public void schedulePeriodic(ChatIndexer indexer) {
        enqueuer.enqueuePeriodic("chat-indexer", indexer, 60_000);
    }

    // 🔥 带优先级的任务
    public void sendSmsWithPriority(Sms sms, String priority) {
        enqueuer.enqueueWithPriority("sms-queue", priority, sms);
    }
}
```

### 3. 创建任务监听器

使用 `@RqueueListener` 注解标记处理方法：

```java
@Component
@Slf4j
public class TaskListener {

    // 简单队列监听
    @RqueueListener(value = "simple-queue")
    public void handleSimpleMessage(String message) {
        log.info("收到消息: {}", message);
    }

    // 带重试和死信队列的任务
    @RqueueListener(
        value = "order-queue",
        numRetries = "3",                          // 最多重试3次
        deadLetterQueue = "failed-orders",         // 死信队列
        concurrency = "5-10"                       // 动态并发 5-10
    )
    public void handleOrder(Order order) {
        log.info("处理订单: {}", order);
        // 业务逻辑
    }

    // 带优先级的队列
    @RqueueListener(
        value = "sms-queue",
        priority = "critical=10,high=8,medium=4,low=1"
    )
    public void handleSms(Sms sms) {
        log.info("处理短信: {}", sms);
    }

    // 长任务支持（定期签入）
    @RqueueListener(value = "long-task-queue")
    public void handleLongTask(
        TaskData data,
        @Header(RqueueMessageHeaders.JOB) Job job
    ) {
        log.info("开始处理长任务: {}", data);
        
        // 定期签入，防止任务超时
        job.checkIn("任务处理中...");
        processLongRunningTask(data);
        job.checkIn("任务完成");
    }
}
```

### 4. API 端点测试

示例应用提供了以下测试接口：

| 请求 | 描述 | 查询参数 |
|------|------|----------|
| `GET /job` | 发送后台任务 | q=队列名, msg=消息内容 |
| `GET /job-delay` | 发送延迟任务 | q=队列名, msg=消息内容, delay=延迟毫秒数 |
| `GET /push` | 发送到任意队列 | q=队列名, msg=消息内容, numRetries=重试次数(可选), delay=延迟(可选) |

**测试示例：**
```bash
# 立即发送任务
curl "http://localhost:8080/job?q=job-queue&msg=Hello+World"

# 延迟 2 秒发送任务
curl "http://localhost:8080/job-delay?q=job-queue&msg=Delayed+Message&delay=2000"

# 发送到指定队列
curl "http://localhost:8080/push?q=simple-queue&msg=Test+Message"
```

---

## 📊 监控与仪表板

### Web 管理仪表板

访问地址：**http://localhost:8080/rqueue**

仪表板提供以下功能：
- 📈 **队列统计**：查看各队列的消息数量、处理速率
- 🔍 **消息探索**：浏览等待执行的消息详情
- 📋 **任务历史**：查看最近执行的任务记录
- ⏱️ **延迟分析**：监控消息处理延迟
- 🎯 **实时状态**：查看正在执行、排队中、已调度的任务

![Dashboard Statistics](https://raw.githubusercontent.com/sonus21/rqueue/master/docs/static/stats-graph.png)

### Grafana 监控

系统集成了 Micrometer 指标，可以配合 Prometheus + Grafana 进行监控：

![Grafana Dashboard](https://raw.githubusercontent.com/sonus21/rqueue/master/docs/static/grafana-dashboard.png)

**关键指标：**
- 队列消息数量（待处理、处理中、已调度）
- 消息处理速率
- 任务执行时间
- 重试次数
- 死信队列消息数

### 队列探索

![Queue Explore](https://raw.githubusercontent.com/sonus21/rqueue/master/docs/static/queue-explore.png)

### 任务详情

![Jobs Detail](https://raw.githubusercontent.com/sonus21/rqueue/master/docs/static/jobs.png)

---

## 👨‍💻 开发指南

### 环境准备

```bash
# 确认 Java 版本
java -version  # 应该显示 Java 21

# 确认 Gradle 版本
./gradlew --version

# 启动 Redis
docker run -d --name redis -p 6379:6379 redis:latest
```

### 代码规范

本项目使用 **Palantir Java Format** 进行代码格式化：

```bash
# 格式化所有 Java 代码
./gradlew formatJava

# 检查代码格式（不修改）
./gradlew checkFormatJava
```

**注意**：提交代码前请确保代码已格式化。

### 构建与测试

```bash
# 清理并构建
./gradlew clean build

# 仅编译，跳过测试
./gradlew build -x test

# 运行所有测试
./gradlew test

# 运行特定模块测试
./gradlew rqueue-core:test

# 生成测试覆盖率报告
./gradlew codeCoverageReport

# 查看覆盖率报告
# 报告位置: build/reports/jacoco/test/html/index.html
```

### 运行示例应用

```bash
# Spring Boot 示例
./gradlew rqueue-spring-boot-example:bootRun

# Spring Boot WebFlux 示例
./gradlew rqueue-spring-boot-reactive-example:bootRun

# 传统 Spring MVC 示例
./gradlew rqueue-spring-example:bootRun
```

### Docker 部署

项目提供了 Docker 配置：

```bash
# 使用 docker-compose 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 贡献代码

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

**提交前请确保：**
- ✅ 代码已格式化 (`./gradlew formatJava`)
- ✅ 所有测试通过 (`./gradlew test`)
- ✅ 代码覆盖率符合要求

---

## ❓ 常见问题

### 1. Redis 连接失败

**症状**：应用启动时抛出 Redis 连接异常

**解决方案**：
```bash
# 检查 Redis 是否运行
docker ps | grep redis

# 测试 Redis 连接
docker exec -it redis redis-cli ping
# 应该返回 PONG

# 如果未运行，启动 Redis
docker run -d --name redis -p 6379:6379 redis:latest
```

### 2. 端口冲突

**症状**：8080 端口已被占用

**解决方案**：在 `application.yml` 中修改端口
```yaml
server:
  port: 8081  # 改为其他可用端口
```

### 3. 测试失败

**症状**：运行 `./gradlew test` 时部分测试失败

**解决方案**：
```bash
# 清理后重新测试
./gradlew clean test

# 查看详细测试日志
./gradlew test --info

# 运行特定测试类
./gradlew test --tests "com.github.sonus21.rqueue.core.YourTestClass"
```

### 4. 构建速度慢

**解决方案**：
```bash
# 跳过测试快速构建
./gradlew build -x test

# 使用 Gradle 守护进程（默认启用）
./gradlew build --daemon

# 并行构建
./gradlew build --parallel
```

### 5. 任务未执行

**可能原因**：
- Redis 连接配置错误
- 监听器未正确注册
- 队列名称不匹配

**排查步骤**：
1. 检查 Redis 连接是否正常
2. 确认 `@RqueueListener` 注解的 `value` 与发送时的队列名一致
3. 查看应用日志是否有错误信息
4. 访问仪表板 http://localhost:8080/rqueue 查看队列状态

---

## 📚 更多资源

### 官方文档
- [完整文档](https://sonus21.github.io/rqueue)
- [配置指南](docs/configuration/configuration.md)
- [Redis 配置](docs/configuration/redis-configuration.md)
- [重试与退避策略](docs/configuration/retry-and-backoff.md)
- [消息处理](docs/message-handling/message-handling.md)
- [消息去重](docs/message-handling/message-deduplication.md)
- [中间件](docs/message-handling/middleware.md)
- [回调与事件](docs/callback-and-events.md)
- [监控指南](docs/monitoring.md)
- [迁移指南](docs/migrations.md)
- [常见问题](docs/faq.md)

### 示例项目
- [Spring Boot 示例](rqueue-spring-boot-example/README.md)
- [Spring Boot WebFlux 示例](rqueue-spring-boot-reactive-example/README.md)
- [Spring MVC 示例](rqueue-spring-example/README.md)

### 相关链接
- [发布版本](https://github.com/sonus21/rqueue/releases)
- [问题追踪](https://github.com/sonus21/rqueue/issues)
- [Maven Central](https://repo1.maven.org/maven2/com/github/sonus21/)

---

## 📄 许可证

本项目采用 **Apache License 2.0** 开源许可证。

版权所有 © 2019-2026

有关详细信息，请参阅 [LICENSE](LICENSE) 文件。

---

## 🤝 支持与反馈

如有任何问题、建议或发现 Bug，欢迎：
- 📝 [提交 Issue](https://github.com/sonus21/rqueue/issues/new/choose)
- 💬 参与讨论
- 🌟 给项目点个 Star

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请考虑给它一个 Star！**

Made with ❤️ by the Rqueue Community

</div>
