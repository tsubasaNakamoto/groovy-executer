# groovy-executer（Java）

一个用 **Java（Spring Boot）** 编写的 Groovy 执行平台：
- 输入 Groovy 脚本
- 输入执行参数（空格分隔）
- 返回 `exitCode / stdout / stderr / timedOut`

## 环境要求

- JDK 17+
- Maven 3.9+
- 本机已安装 `groovy` 命令并配置到 PATH

## 启动

```bash
mvn spring-boot:run
```

启动后打开：`http://localhost:8080`

## API

### `POST /api/execute`

请求示例：

```json
{
  "script": "println \"Hello, ${args.join(',')}\"",
  "args": ["Alice", "Bob"],
  "timeoutSeconds": 10
}
```

响应示例：

```json
{
  "exitCode": 0,
  "stdout": "Hello, Alice,Bob\n",
  "stderr": "",
  "timedOut": false
}
```

## 实现说明

- 服务端通过 `groovy <temp_script>.groovy ...args` 执行脚本。
- 执行前会检查 `groovy --version`，缺失时返回 400。
- 默认超时 10 秒，可设置范围 `1~120` 秒。
- 脚本以临时文件执行，完成后自动删除。
