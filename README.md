# groovy-executer（Java）

一个用 **Java（Spring Boot）** 编写的 Groovy 执行平台：
- 输入 Groovy 脚本
- 输入执行参数（空格分隔）
- 返回 `exitCode / stdout / stderr / timedOut`

## 环境要求

- JDK 17+
- Maven 3.9+

> 不依赖本机 `groovy` 命令。项目通过 Maven 引入 `org.codehaus.groovy:groovy`，在 JVM 内直接执行脚本。

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

- 服务端使用 `GroovyShell` 执行脚本，而不是启动外部进程。
- `args` 以 `String[]` 形式注入脚本上下文，脚本中可直接使用 `args`。
- 默认超时 10 秒，可设置范围 `1~120` 秒。
- 超时后会取消执行任务并返回 `timedOut=true`。
