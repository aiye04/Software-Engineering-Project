# 网络安全战队协同与教学管理平台

本仓库用于实现“网络安全战队协同与教学管理平台（CTF Team & Coach Hub）”。当前源码按项目根目录组织，设计资料保留在 `开发进度文档/`，实际可运行代码放在 `backend/`、`frontend/`、`deploy/` 和 `docs/` 中。

## 功能范围

本阶段优先实现 MVP：

1. 项目骨架、数据库脚本、统一接口格式。
2. 登录注册、Token 鉴权、角色权限。
3. 管理员用户管理。
4. 赛事、赛题、WriteUp、训练任务、通知等核心业务 CRUD。
5. 教学看板和基础图表展示。
6. WebSocket、缓存、搜索等增强能力预留接口或基础实现。

第 7 部分（完整测试报告、演示 PPT、生产部署手册等交付材料）暂不实施。

## 目录结构

```text
.
├── backend/       # Spring Boot 后端
├── frontend/      # Vue 3 + TypeScript 前端
├── deploy/        # Docker Compose、Nginx 和环境变量示例
├── docs/          # API、数据库、启动说明等开发文档
└── 开发进度文档/  # 原始设计与计划资料
```

## 默认账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `Admin@123456` | 管理员 |
| `teacher` | `Teacher@123456` | 老师 |
| `captain` | `Captain@123456` | 队长 |

## 本地启动

### 后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`。

健康检查：`GET http://localhost:8080/api/health`。

### 前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`。

### Docker 基础服务

```bash
cd deploy
docker compose up -d mysql redis
```

## 文档

- API 草案：`docs/api.md`
- 数据库说明：`docs/database.md`
- 启动说明：`docs/getting-started.md`
