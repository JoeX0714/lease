# AI租赁管理系统

尚庭公寓是一个基于 Spring Boot 的公寓租赁管理后端项目，提供管理端和用户端两套服务，覆盖房源管理、用户认证、预约看房、租赁合同、报修、通知以及 AI 对话等业务。

## 功能概览

- **管理端**：公寓、房间、属性、配套、费用、标签、租期、支付方式和区域信息管理。
- **租赁管理**：租赁合同、合同到期提醒、看房预约和维修工单管理。
- **用户管理**：系统用户、岗位、租客信息和 JWT 登录认证。
- **用户端**：房源浏览、历史记录、预约看房、合同查看、报修、通知和支付方式查询。
- **智能能力**：基于 Spring AI 接入 DeepSeek，支持租赁相关对话和业务工具调用。
- **基础设施**：MySQL 持久化、Redis 缓存、MinIO 对象存储、短信服务、图形验证码和 Knife4j 接口文档。

## 技术栈

| 类别 | 技术 |
| --- | --- |
| 基础框架 | Java 17、Spring Boot 3.5.15、Spring MVC |
| 数据访问 | MyBatis-Plus、MySQL |
| 缓存与认证 | Redis、JWT |
| 文件与消息 | MinIO、阿里云短信 |
| 接口文档 | SpringDoc、Knife4j |
| AI | Spring AI、DeepSeek API |
| 构建工具 | Maven |

## 项目结构

```text
lease/
├── common/                 # 公共配置、异常、登录上下文、工具类和基础组件
├── model/                  # 实体类、枚举和公共数据模型
├── web/
│   ├── web-admin/          # 管理端服务，默认端口 8080
│   └── web-app/            # 用户端服务，默认端口 8081
├── pom.xml                 # Maven 父工程
└── README.md
```

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8+
- Redis 6+
- MinIO（管理端文件上传需要）
- 阿里云短信账号（用户端短信登录需要）
- DeepSeek API Key（AI 对话功能需要）

## 配置说明

服务配置位于：

- `web/web-admin/src/main/resources/application.yml`
- `web/web-app/src/main/resources/application.yml`

启动前请根据本地环境修改 MySQL、Redis 和 MinIO 地址，并准备名为 `lease` 的数据库及对应表结构。

用户端使用以下环境变量配置外部服务：

```text
DEEPSEEK_API_KEY= DeepSeek API Key
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_CHAT_MODEL=deepseek-v4-flash
ALIYUN_ACCESS_KEY_ID=阿里云 AccessKey ID
ALIYUN_ACCESS_KEY_SECRET=阿里云 AccessKey Secret
```

> 安全提示：不要把数据库密码、MinIO 密钥、短信密钥或 AI API Key 提交到公开仓库。建议使用环境变量、Spring 配置文件外置或密钥管理服务，并及时轮换已经暴露过的凭据。

## 构建项目

在项目根目录执行：

```bash
mvn clean package -DskipTests
```

只构建指定服务及其依赖：

```bash
mvn -pl web/web-admin -am package -DskipTests
mvn -pl web/web-app -am package -DskipTests
```

## 启动服务

开发环境可直接使用 Maven 启动：

```bash
# 管理端：http://localhost:8080
mvn -pl web/web-admin -am spring-boot:run

# 用户端：http://localhost:8081
mvn -pl web/web-app -am spring-boot:run
```
也可以先打包，再运行生成的 JAR：

```bash
java -jar web/web-admin/target/web-admin-*.jar
java -jar web/web-app/target/web-app-*.jar
```

## 接口文档

服务启动后，可通过 Knife4j 查看接口文档：

- 管理端：<http://localhost:8080/doc.html>
- 用户端：<http://localhost:8081/doc.html>

## 开发说明

- `common` 和 `model` 是两个公共模块，由 `web` 下的服务模块共同依赖。
- 管理端启用了定时任务，用于处理租赁到期提醒等后台任务。
- 用户端启用了异步处理，并通过环境变量读取 DeepSeek 与阿里云短信配置。
- 当前仓库主要包含后端服务；前端项目需要单独部署并配置对应的 API 地址。
