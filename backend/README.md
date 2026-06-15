# 后端服务说明

本目录是“网络安全战队协同与教学管理平台”的后端 MVP 工程，基于 Maven、Spring Boot 3 和 Java 17 实现。

当前版本为了方便本地运行和课程演示，业务数据暂时保存在内存中，不需要提前配置数据库即可启动。`src/main/resources/db/init.sql` 提供了后续接入关系型数据库时可参考的表结构脚本。

## 运行方式

在 `backend` 目录下执行：

```bash
mvn test
mvn package
mvn spring-boot:run
```

后端默认启动端口为 `8080`。

健康检查接口：

```text
GET http://localhost:8080/api/health
```

## 默认账号

系统启动后会在内存中初始化以下演示账号，密码使用 BCrypt 哈希保存：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `Admin@123456` | `ADMIN` |
| `teacher` | `Teacher@123456` | `TEACHER` |
| `captain` | `Captain@123456` | `CAPTAIN` |

通过注册接口自行注册的新用户，默认角色为 `TRAINEE`。

## 主要接口

所有接口返回统一格式：

```json
{"code":0,"message":"ok","data":{},"timestamp":"2026-01-01T00:00:00Z"}
```

当前 MVP 已实现的接口包括：

- `GET /api/health`
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`，需要请求头 `Authorization: Bearer <token>`
- `GET/POST/PATCH/DELETE /api/users`
- `GET/POST/PUT/DELETE /api/competitions`
- `GET /api/competitions/challenges`
- `POST /api/competitions/{competitionId}/challenges`
- `GET/POST /api/writeups?category=web`
- `GET/POST /api/training/tasks`
- `POST /api/training/tasks/{taskId}/submissions`，需要 Bearer Token
- `GET /api/training/submissions`
- `GET/POST /api/notifications`
- `GET /api/dashboard`

## 认证说明

当前 Token 使用类似 JWT 的格式：`payload.signature`，并通过 HMAC-SHA256 签名。

生产环境或正式部署前，建议完成以下改造：

- 修改默认的 `app.token-secret`。
- 按角色补充更完整的接口级权限校验。
- 将内存数据服务替换为基于数据库的持久化实现。
