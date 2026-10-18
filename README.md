# Crabapple Standard MS（海棠标准管理服务）

基于 [crabapple-commons](https://github.com/xuechong1989/crabapple-commons) 构建的标准管理服务示例，演示如何用 `crabapple-commons` 快速搭建一个具备用户体系、短信登录、配置管理、系统日志与安全过滤的 Spring Boot 服务。

## 功能

- **用户体系**：注册、账号密码登录、短信验证码登录、退出登录、用户列表
- **短信通道**：腾讯云短信，未配置时自动停用（默认关闭）
- **配置管理**：`t_properties` 键值配置的读写与分页查询
- **系统日志**：登录、注册、发短信等关键操作异步落库
- **安全能力**：XSS / CSRF / 同源 / IP 白名单 / 限流 / 上传 Webshell 防护 / 传参加密（默认关闭）
- **缓存**：本地缓存默认开启，Redis 可选
- **数据库**：MySQL / Oracle / SQL Server / DB2 / 达梦 / 人大金仓（MyBatis + Druid）

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 语言 | Java 21 |
| 框架 | Spring Boot 4 |
| 持久层 | MyBatis 3 + Druid |
| 缓存 | 本地缓存 / Redis（可选） |
| 构建 | Maven 3.9+ |
| 基础库 | [crabapple-commons](https://github.com/xuechong1989/crabapple-commons) |

## 目录结构

```
crabapple-standardms
├── pom.xml
└── src/main
    ├── java/com/cdk/standardms
    │   ├── common          配置、领域对象、工具类
    │   └── module          用户 / 登录 / 短信 / 配置 / 日志 / 首页
    └── resources
        └── application.properties.example
```

## 快速开始

### 1. 安装基础库

```bash
git clone https://github.com/xuechong1989/crabapple-commons.git
cd crabapple-commons
mvn clean install
```

### 2. 准备配置

```bash
cd crabapple-standardms
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

按实际环境修改数据库连接；短信、Redis、传参加密默认关闭，需要时再填写对应密钥。完整配置项见 [application.properties.example](src/main/resources/application.properties.example)。

### 3. 初始化数据库

在 MySQL 中创建数据库（默认 `crabapple_standardms`），并按下表准备业务表。字段定义可参考 `src/main/java/com/cdk/standardms/module/**/*Mapper.xml` 与对应 DAO。

| 表 | 用途 |
| --- | --- |
| `t_user` | 用户账号、手机号、邮箱、BCrypt 口令摘要等 |
| `t_properties` | 键值配置 |
| `t_sms` | 短信验证码与有效期 |
| `t_systemlog` | 系统操作日志 |

> 口令字段需能容纳 BCrypt 摘要（60 字符），建议 `VARCHAR(100)`。

### 4. 启动

```bash
mvn spring-boot:run
# 或
mvn clean package
java -jar target/crabapple-standardms-21-4.2.00.jar
```

默认端口 `8080`，健康检查：`POST /api/crabapple/standardms/index/heart`。

## 主要接口

| 接口 | 说明 |
| --- | --- |
| `POST /api/crabapple/standardms/login/register` | 注册 |
| `POST /api/crabapple/standardms/login/loginByPwd` | 账号密码登录 |
| `POST /api/crabapple/standardms/login/loginBySms` | 短信验证码登录 |
| `POST /api/crabapple/standardms/login/sendSms` | 发送短信验证码 |
| `POST /api/crabapple/standardms/login/logout` | 退出登录 |
| `POST /api/crabapple/standardms/user/getList` | 用户列表 |
| `POST /api/crabapple/standardms/properties/getList` | 配置列表 |
| `POST /api/crabapple/standardms/systemlog/getList` | 系统日志列表 |

请求/响应统一使用 `S2cDto` 包装。

## 需要自行配置的密钥

仓库不包含任何真实密钥。首次部署请复制 [application.properties.example](src/main/resources/application.properties.example) 并填写：

| 配置项 | 说明 |
| --- | --- |
| `db.password` | 数据库口令 |
| `sms_tencent_secretid` / `sms_tencent_secretkey` | 腾讯云短信凭据，启用短信时必填 |
| `cache_redis_password` / `cache_redis_cipher_key` | Redis 口令与 SM4 密钥，启用 Redis 时必填 |
| `security_tc_c2s_*` / `security_tc_s2c_*` | 传参加密的 SM2/SM4 密钥，启用时必填 |
| `security_csrf_token_submerge_key` | CSRF 混淆密钥，启用 CSRF 时必填 |
| `CRABAPPLE_PROPERTIES_SM4_KEY` / `CRABAPPLE_PROPERTIES_SM4_IV` | `t_properties` 中 `cipher=yes` 时的 SM4 密钥与 IV（环境变量，未配置则跳过加密配置项） |

> 短信、Redis、传参加密默认关闭；未填写的配置项不会使用任何默认密钥。

## 安全说明

- 仓库中不包含任何真实密钥、口令或内部地址；所有敏感配置均通过 `application.properties` 注入。
- 口令使用 BCrypt 摘要存储，登录时校验摘要，不保存明文；如从包含明文口令的旧库升级，请先重置口令，`password_` 字段长度至少 60。
- 短信验证码不会写入日志。
- 传参加密、CSRF 等能力默认关闭；启用前请自行生成并保管 SM2/SM4 密钥。
- 生产部署请通过环境变量或外部配置中心注入密钥，不要提交到 Git。
- `t_properties.cipher_=yes` 的配置项需要 `CRABAPPLE_PROPERTIES_SM4_KEY` / `CRABAPPLE_PROPERTIES_SM4_IV`；未设置时不会使用任何内置密钥。

## 许可证

[Apache License 2.0](LICENSE)。第三方组件许可见 [NOTICE](NOTICE)。
