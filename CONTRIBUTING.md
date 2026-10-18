# 参与贡献

感谢参与 Crabapple Standard MS！

## 环境准备

- JDK 21
- Maven 3.9+
- 本地已安装 `crabapple-commons`（见 [README](README.md)）
- MySQL / Redis 等外部依赖按需准备

## 构建与测试

```bash
mvn clean package
```

## 约定

- 4 空格缩进，保持现有包结构与命名；
- 新增接口时同步补充 README 的接口表；
- 配置项统一写入 `application.properties.example` 并在 README 说明；
- 严禁提交真实密钥、口令、证书、内部地址或用户数据；
- 涉及安全过滤、登录、短信等改动时，请在 PR 中说明影响范围。

## 许可证

贡献的代码将按 [Apache License 2.0](LICENSE) 许可发布。
