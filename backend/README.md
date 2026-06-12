# Spring Boot 后端 MVP

> **Language:** 中文 | [English](README_EN.md)

本目录包含一个 Maven + Spring Boot 3 + Java 17 后端 MVP。数据存储在内存中，因此无需配置数据库即可编译运行；`src/main/resources/db/init.sql` 提供了未来关系型数据库的参考结构。

## 运行

```bash
mvn test
mvn package
mvn spring-boot:run
```

应用默认监听 `8080` 端口。

## 默认账号

系统预设账号使用 BCrypt 密码哈希存储在内存中：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `Admin@123456` | `ADMIN`（管理员） |
| `teacher` | `Teacher@123456` | `TEACHER`（教师） |
| `captain` | `Captain@123456` | `CAPTAIN`（队长） |

自行注册的新用户默认获得 `TRAINEE`（学员）角色。

## 主要 API

所有响应均使用统一格式：

```json
{"code":0,"message":"ok","data":{},"timestamp":"2026-01-01T00:00:00Z"}
```

已实现的 MVP 端点：

- `GET /api/health`
- `POST /api/auth/login`（登录）
- `POST /api/auth/register`（注册）
- `GET /api/auth/me`，需携带 `Authorization: Bearer <token>`
- `GET/POST/PATCH/DELETE /api/users`
- `GET/POST/PUT/DELETE /api/competitions`
- `GET /api/competitions/challenges`
- `POST /api/competitions/{competitionId}/challenges`
- `GET/POST /api/writeups?category=web`
- `GET/POST /api/training/tasks`
- `POST /api/training/tasks/{taskId}/submissions`，需携带 Bearer token
- `GET /api/training/submissions`
- `GET/POST /api/notifications`
- `GET /api/dashboard`

Token 格式类似 JWT（`payload.signature`），使用 HMAC-SHA256 签名。生产环境中，请替换默认的 `app.token-secret`、按角色添加授权检查，并使用数据库支持的 Repository 持久化数据。
