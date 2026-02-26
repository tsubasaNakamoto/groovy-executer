# 执行参数上下文（argsContext）教程

本文说明如何在 `groovy-executer` 中使用“执行参数上下文”。

## 1. 核心概念

执行脚本时，系统会同时传两类参数：

- `args`：兼容旧逻辑的字符串数组。
- `argsContext`：保留原始结构的上下文对象（List / Map / Primitive / String）。

在 Groovy 脚本中，推荐优先使用 `context`（或别名 `params`）读取上下文。

## 2. 脚本里可直接使用的变量

执行时会注入以下变量：

- `context`：上下文对象（推荐使用）
- `params`：`context` 的别名
- `list`：当 `context` 是 List/Array 时可用
- `json`：当 `context` 是 JSON Object/Map 时可用
- `args`：字符串数组（`String[]`，兼容旧逻辑）
- `argsList`：字符串列表（`List<String>`）
- `out` / `err`：输出流

## 3. 前端输入到上下文的映射

执行参数输入框内容会被识别并映射：

- `[]`、`[1,2,3]`、`["A","B"]` → `List/Array`
- `{"name":"Tom","age":18}` → `JSON Object (Map)`
- `123`、`true`、`null` → `JSON Primitive`
- 非 JSON 文本（如 `hello`）→ 纯文本

## 4. 常用脚本示例

### 示例 A：List/Array（如输入 `[1,2,3]`）

```groovy
println "context class=${context.getClass().name}"
println "first=${context.get(0)}"
println "list second=${list.get(1)}"
println "args[0]=${args[0]}"   // 兼容字符串参数
```

### 示例 B：JSON Object（如输入 `{"user":"alice","age":20}`）

```groovy
println "user=${context.get('user')}"
println "age=${json.get('age')}"
```

### 示例 C：纯文本（如输入 `hello world`）

```groovy
println "context=${context}"          // hello world
println "args[0]=${args[0]}"          // hello world
println "argsList=${argsList}"
```

## 5. 接口调用示例

### `POST /api/execute`

```json
{
  "script": "println context.get(0)",
  "args": ["1", "2", "3"],
  "argsContext": [1, 2, 3],
  "timeoutSeconds": 10
}
```

### `POST /api/scripts/{id}/execute`

```json
{
  "argsContext": {"name": "alice", "tags": ["dev", "ops"]},
  "timeoutSeconds": 10
}
```

## 6. 已保存脚本参数的回退行为

当执行 `POST /api/scripts/{id}/execute` 且请求里没有传 `args`/`argsContext` 时：

1. 系统会读取脚本保存的 `executeArgsJson` 文本。
2. 若是合法 JSON，则按 JSON 结构作为 `context` 注入。
3. 若不是合法 JSON，则按纯文本注入 `context`。

## 7. 实践建议

- 新脚本优先用 `context` / `params` 读取参数。
- 需要向后兼容旧脚本时，继续保留 `args` 的用法。
- 复杂对象建议传 JSON Object；顺序参数建议传 List/Array。
