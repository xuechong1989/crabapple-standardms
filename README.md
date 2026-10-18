<p align="center"><img src="assets/logo-128.png" width="96" height="96" alt="Crabapple Standard MS" /></p>

# Crabapple Standard MS（海棠标准管理服务）

> 品牌色：海棠红 `#B3123B` · 海棠粉 `#E63B5F` · 深红 `#8E0E2E`

基于 [crabapple-commons](https://github.com/xuechong1989/crabapple-commons) 构建的 Spring Boot 4 标准管理服务示例，包含用户、登录、短信验证码、配置、系统日志与安全过滤。

## 项目定位与适用范围

这是一个 **后端 HTTP 服务**，默认监听 `8080`，不是桌面应用，也不带前端页面。

- 适合作为内部管理服务/用户中心的起步模板；
- 适合演示 `crabapple-commons` 的 Spring Boot、MyBatis、Druid、安全过滤集成；
- 默认只提供 JSON API，不包含管理后台 UI；
- 当前没有单元测试，生产使用前需要补充集成测试、认证鉴权、审计和容量验证；
- 默认不要直接暴露到公网；公网部署应放在网关/反向代理后，并启用 TLS、限流和访问控制。

## 功能范围

- 用户注册、账号/手机号/邮箱 + 密码登录、短信验证码登录、退出、用户列表
- 腾讯云短信通道（默认关闭）
- `t_properties` 键值配置查询
- 登录/注册/短信等操作日志异步落库
- XSS / CSRF / 同源 / IP 白名单 / 限流 / 上传 Webshell 防护 / 传参加密（默认关闭）
- 本地缓存默认开启，Redis 可选
- MyBatis + Druid；MySQL 为默认示例，其他数据库需自行适配

## 技术栈

| 层级 | 技术 |
|---|---|
| 语言 | Java 21 |
| 框架 | Spring Boot 4.1 |
| 持久层 | MyBatis 3 + Druid |
| 缓存 | 本地缓存 / Redis（可选） |
| 构建 | Maven 3.9+ |
| 基础库 | [crabapple-commons](https://github.com/xuechong1989/crabapple-commons) |

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8.x（默认；其他数据库需改 `db.type` / 驱动 / SQL 方言）
- 可选：Redis、腾讯云短信账号

## 部署步骤

### 1. 先安装基础库

`pom.xml` 依赖 `xc.crabapple:crabapple-commons:21-4.2.00`。首次构建前：

```bash
git clone https://github.com/xuechong1989/crabapple-commons.git
cd crabapple-commons
mvn clean install
```

如果已经有私有 Maven 仓库中的 `crabapple-commons`，跳过这一步。

### 2. 初始化数据库

创建数据库并执行仓库内的建表脚本：

```bash
mysql -u root -p -e "CREATE DATABASE crabapple_standardms DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p crabapple_standardms < src/main/resources/schema.sql
```

建表脚本仅用于新库；已有数据请自行迁移，不要直接覆盖生产库。

| 表 | 用途 |
|---|---|
| `t_user` | 用户账号、手机号、邮箱、BCrypt 口令摘要 |
| `t_properties` | 键值配置 |
| `t_sms` | 短信验证码与过期时间 |
| `t_systemlog` | 系统操作日志 |

`t_user.id_`、`t_systemlog.id_` 使用 `AUTO_INCREMENT`；注册和日志插入不依赖应用侧雪花 ID。

### 3. 准备配置

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

至少修改：

```properties
db.url=jdbc:mysql://127.0.0.1:3306/crabapple_standardms?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
db.username=change-me
db.password=change-me
server.port=8080
```

`application.properties` 已被 `.gitignore` 忽略，不要提交真实口令。

### 4. 构建并启动

```bash
cd crabapple-standardms
mvn clean package
java -jar target/crabapple-standardms-21-4.2.00.jar
```

开发时也可以：

```bash
mvn spring-boot:run
```

启动目录建议使用项目根目录，因为默认日志路径是相对路径 `./logs`。

健康检查：

```bash
curl -X POST http://127.0.0.1:8080/api/crabapple/standardms/index/heart
```

## 首次用户初始化

**本服务没有默认管理员账号，也不会自动创建用户。**

注册接口要求短信验证码，而短信通道默认关闭，所以默认配置下无法直接完成自助注册。二选一：

### 方式 A：启用腾讯云短信后注册

在 `application.properties` 中填写并启用：

```properties
sms_tencent_enable=yes
sms_tencent_secretid=...
sms_tencent_secretkey=...
sms_tencent_appid=...
sms_tencent_templateid=...
sms_tencent_sign=...
```

然后调用 `/login/sendSms` 获取验证码，再调用 `/login/register`。

### 方式 B：直接插入一个 BCrypt 用户

用任意 BCrypt 工具生成 60 字符摘要，然后插入：

```sql
INSERT INTO t_user
(account_, username_, password_, mobile_, email_, gender_, birthday_, login_count_, status_)
VALUES
('admin', '管理员', '<BCrypt hash>', '13800000000', 'admin@example.com', '男', '1990-01-01', 0, 'enable');
```

> 不要在数据库保存明文口令；`password_` 至少 `VARCHAR(100)`。

## 认证与接口调用

除公开接口外，其余接口要求登录 token。登录成功后 token 同时写入 cookie，并放在响应 `S2cDto` 的 `token` 参数中；后续请求可带 cookie，或带 `token` 请求参数。

公开接口：

- `POST /api/crabapple/standardms/index/main`
- `POST /api/crabapple/standardms/index/heart`
- `POST /api/crabapple/standardms/login/register`
- `POST /api/crabapple/standardms/login/sendSms`
- `POST /api/crabapple/standardms/login/loginByPwd`
- `POST /api/crabapple/standardms/login/loginBySms`

其余接口（`user/getList`、`properties/getList`、`systemlog/getList`、`logout`）需要有效 token。

### 接口格式

控制器接收的是 **form 参数 `text`**（JSON 字符串），不是裸 JSON body。例如：

```bash
# 健康检查
curl -X POST http://127.0.0.1:8080/api/crabapple/standardms/index/heart

# 账号密码登录：text 是 JSON 字符串
curl -c cookies.txt -X POST http://127.0.0.1:8080/api/crabapple/standardms/login/loginByPwd \
  --data-urlencode 'text={"account":"admin","password":"your-password"}'

# 登录后带 cookie 查询用户列表
curl -X POST http://127.0.0.1:8080/api/crabapple/standardms/user/getList \
  -b cookies.txt \
  --data-urlencode 'text={"startIndex":0,"pageSize":20}'
```

注册请求的 `text` 至少包含：

```json
{
  "account": "user1",
  "username": "用户1",
  "password": "明文口令",
  "repassword": "明文口令",
  "mobile": "13800000000",
  "email": "user1@example.com",
  "gender": "男",
  "birthday": "1990-01-01",
  "smsValidCode": "123456"
}
```

短信登录使用两个 form 参数：`text`（含手机号/账号/邮箱的 JSON）与 `smsValidCode`。

### 主要接口

| 接口 | 说明 |
|---|---|
| `POST /api/crabapple/standardms/login/register` | 注册（需要短信验证码） |
| `POST /api/crabapple/standardms/login/loginByPwd` | 账号/手机号/邮箱 + 密码登录 |
| `POST /api/crabapple/standardms/login/sendSms` | 发送短信验证码 |
| `POST /api/crabapple/standardms/login/loginBySms` | 短信验证码登录 |
| `POST /api/crabapple/standardms/login/logout` | 退出 |
| `POST /api/crabapple/standardms/user/getList` | 用户列表（脱敏） |
| `POST /api/crabapple/standardms/properties/getList` | 配置分页 |
| `POST /api/crabapple/standardms/systemlog/getList` | 系统日志分页 |

请求/响应统一使用 `S2cDto` 包装。

## 配置说明

完整配置见 [application.properties.example](src/main/resources/application.properties.example)。

> 配置读取顺序：`t_properties` 中 `alias_` 命中且值非空时优先；没有记录或值为空时回退到 `application.properties`。

| 配置 | 说明 |
|---|---|
| `server.port` | 服务端口，默认 `8080` |
| `db.type` / `db.url` / `db.username` / `db.password` | 数据库连接 |
| `db.package` | Mapper 接口包，`xc.crabapple.standardms.module` |
| `db.xml.url` | Mapper XML 路径，`xc/crabapple/standardms/module/**/*.xml` |
| `log.file.path` | 日志目录，默认 `./logs` |
| `cache_redis_enable` | 是否启用 Redis，默认 `no` |
| `cache_redis_password` / `cache_redis_cipher_key` | Redis 口令与 SM4 密钥，启用时填写 |
| `sms_tencent_*` | 腾讯云短信配置，默认关闭 |
| `security_crossdomain_value` | 允许的跨域来源，示例为 `http://localhost:8080` |
| `security_xss_enable` / `security_csrf_enable` / `security_sameorigin_enable` | 安全开关，默认关闭，启用前请验证客户端兼容性 |
| `security_tc_*` | 传参加密的 SM2/SM4 配置，默认关闭 |
| `CRABAPPLE_PROPERTIES_SM4_KEY` / `CRABAPPLE_PROPERTIES_SM4_IV` | 环境变量；`t_properties.cipher_=yes` 时用于解密配置值 |

### `t_properties` 加密配置

如果 `t_properties` 中存在 `cipher_='yes'` 的记录，必须设置环境变量：

```bash
export CRABAPPLE_PROPERTIES_SM4_KEY='<16位密钥>'
export CRABAPPLE_PROPERTIES_SM4_IV='<16位IV>'
```

未设置时，`PropertiesServiceImpl` 会记录错误并跳过该加密配置，不会使用任何硬编码密钥。

## 生产部署建议

- 使用独立数据库账号，不要使用 `root`；只授予业务库所需权限。
- 通过环境变量/配置中心注入数据库口令、短信密钥、SM2/SM4 密钥。
- 放在 Nginx/网关后，启用 HTTPS、请求体大小限制和访问控制。
- `security_crossdomain_value` 不要设为 `*`；按实际前端域名配置。
- 日志目录、数据库和 `application.properties` 纳入备份；升级前先备份数据库。
- 当前没有内置管理后台、单元测试和完整的 RBAC；生产前请补充。
- 服务默认不处理 TLS，请由反向代理终止 TLS。

## 测试与限制

```bash
mvn clean package
```

- 当前 `src/test` 为空，CI 只做编译/打包，不代表业务逻辑已被测试覆盖。
- 注册依赖短信验证码；未配置短信时请用 SQL 初始化用户。
- 默认安全过滤开关较宽松，面向公网前必须逐项评估。
- 数据库方言以 MySQL 8 为准；其他数据库需要自行调整 DDL 和驱动。

## 许可证

[Apache License 2.0](LICENSE)。第三方组件许可见 [NOTICE](NOTICE)。
