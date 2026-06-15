# Repository Guidelines

## Project Structure & Module Organization

This repository is a front-end/back-end separated CTF team training platform.

- `backend/`: Spring Boot 3 backend. Main code is under `src/main/java/com/example/backend/`, grouped by `controller`, `service`, `model`, `dto`, and `exception`.
- `backend/src/main/resources/`: `application.yml` and `db/init.sql`.
- `backend/src/test/`: Spring Boot tests.
- `frontend/`: Vue 3 + TypeScript + Vite app. Pages live in `src/views/`, API code in `src/api/`, routing in `src/router/`, state in `src/stores/`, and styles in `src/styles/`.
- `docs/`: API, database, and startup notes.
- `deploy/`: Docker Compose and Nginx deployment files.
- `开发进度文档/`: project progress and coursework documentation.

## Build, Test, and Development Commands

Backend:

```bash
cd backend
mvn test
mvn spring-boot:run
```

`mvn test` runs backend tests. `mvn spring-boot:run` starts the API at `http://localhost:8080`.

Frontend:

```bash
cd frontend
npm install
npm run dev
npm run build
```

`npm run dev` starts Vite at `http://localhost:5173`. `npm run build` runs TypeScript checks and builds production assets.

Docker services, optional for local MVP work:

```bash
cd deploy
docker compose up -d mysql redis
```

## Coding Style & Naming Conventions

Use Java 17-compatible backend code and keep Spring classes in existing layered packages. Name controllers as `*Controller`, services as `*Service`, and DTOs descriptively, for example `Result` or `UserView`.

For Vue, use PascalCase single-file components such as `EventDetailView.vue`. Keep types in `frontend/src/api/types.ts`, API helpers in `client.ts`, and route pages under `views/`. Avoid duplicating API response unwrapping logic.

## Testing Guidelines

Backend tests use Spring Boot Test and should be placed under `backend/src/test/java`. Name test classes `*Tests.java`. Run `mvn test` before submitting backend changes.

Frontend validation relies on TypeScript and Vite. Run `npm run build` before submitting UI or API-client changes. For user-facing changes, manually verify login, dashboard, CRUD pages, and `/api` proxy behavior.

## Commit & Pull Request Guidelines

Recent history uses short scoped messages such as `[docs] 更新阿华进度分工说明`. Prefer:

```text
[frontend] 完善训练任务交互
[backend] 修复登录校验
[docs] 更新测试说明
```

Pull requests should include a summary, affected modules, test results, and screenshots for visible UI changes. Link related issues or coursework tasks when available.

## Security & Configuration Tips

Do not commit secrets, real passwords, or local IDE files. Keep production token secrets out of `application.yml`; use environment variables for real deployments. Current data is in memory for MVP demonstration, so do not assume persistence until MySQL integration is implemented.
