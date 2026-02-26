# groovy-executer（Java + MyBatis + MySQL）

支持两类能力：
1. 执行 Groovy 脚本（JVM 内 Groovy 依赖执行，不调用本地 groovy 命令）。
2. 脚本管理 CRUD（MyBatis + MySQL）。

## 环境要求

- JDK 17+
- Maven 3.9+
- 本地 MySQL（`127.0.0.1:3306`）

## 数据库说明

- 连接配置在 `src/main/resources/application.yml`：默认连接 `groovy_executer` 库，用户名/密码 `root/root`。
- 建表 SQL 已写在工程里：`src/main/resources/db/schema.sql`。
- Spring Boot 启动时会自动执行 schema SQL（`spring.sql.init.mode=always`）。

## 启动

```bash
mvn spring-boot:run
```

访问：`http://localhost:8080`

## API

### 执行脚本

- `POST /api/execute`：执行请求体中的脚本内容。
- `POST /api/scripts/{id}/execute`：按已保存脚本 ID 执行。

### 参数上下文教程

- `docs/args-context-tutorial.md`：`argsContext` 的使用说明、变量清单和脚本示例。

### 脚本 CRUD（MyBatis SQL 实现）

- `GET /api/scripts`：脚本列表
- `GET /api/scripts/{id}`：脚本详情
- `POST /api/scripts`：新增脚本
- `PUT /api/scripts/{id}`：更新脚本
- `DELETE /api/scripts/{id}`：删除脚本

新增请求示例：

```json
{
  "name": "hello",
  "description": "demo script",
  "content": "println \"Hello, ${args.join(',')}\""
}
```
