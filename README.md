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

## 运行环境要求

### 必需环境

| 环境 | 建议版本 | 用途 | 当前本机检测结果 |
| --- | --- | --- | --- |
| Git | 2.x | 克隆和管理代码 | 已安装：`2.52.0.windows.1` |
| JDK | 17 | 运行 Spring Boot 后端 | 已安装：Java `21.0.8`，通常可兼容运行；如遇问题建议安装 JDK 17 |
| Maven | 3.8+ / 3.9+ | 构建、测试和启动后端 | IDEA 内置 Maven 可用；命令行暂未配置 `mvn` |
| Node.js | 20+ | 运行 Vue 3 前端 | 已安装：`v24.14.0` |
| npm | 10+ | 安装和构建前端依赖 | 已安装：`11.9.0` |

### 可选环境

| 环境 | 建议版本 | 用途 | 当前本机检测结果 |
| --- | --- | --- | --- |
| Docker Desktop | 支持 Docker Compose | 一键启动 MySQL、Redis、后端和 Nginx | 未安装，当前无法执行 `docker compose up` |
| MySQL | 8.0 | 后续接入数据库持久化时使用 | 已安装：MySQL `8.0.45` |
| Redis | 7.x | 后续缓存、实时协同等增强功能使用 | 未安装 |

### 当前缺失项

当前机器至少还缺少或需要配置以下环境：

1. Maven 命令行配置：可以直接在 IDEA 的 Maven 面板中运行后端；如果希望在命令行执行 `mvn test` 或 `mvn spring-boot:run`，需要把本机 Maven 安装目录或 IDEA 内置 Maven 的 `bin` 目录加入 `PATH`，也可以后续为项目补充 Maven Wrapper。
2. Docker Desktop：如果希望按 `deploy/docker-compose.yml` 启动 MySQL、Redis、后端和 Nginx，需要安装 Docker Desktop。
3. Redis：如果不使用 Docker，需要单独安装 Redis；当前 MVP 后端暂未强依赖 Redis，本地基础演示可先不装。

已使用 IDEA 内置 Maven 执行过后端测试，`backend` 模块当前 `mvn test` 通过。

## 本地启动

### 后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`。

健康检查：`GET http://localhost:8080/api/health`。

如果提示 `mvn` 命令不存在，可以在 IDEA 的 Maven 面板中运行，或将 IDEA 内置 Maven 的 `bin` 目录加入系统 `PATH`。

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
