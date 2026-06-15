# 启动说明

## 前置环境

- JDK 17+
- Maven 3.9+
- Node.js 20+
- Docker / Docker Compose（可选）

## 后端启动

```bash
cd backend
mvn spring-boot:run
```

访问 `http://localhost:8080/api/health` 验证后端是否启动成功。

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认通过 Vite 代理请求 `/api` 到后端。

## 默认账号

- 管理员：`admin / Admin@123456`
- 老师：`teacher / Teacher@123456`
- 队长：`captain / Captain@123456`
