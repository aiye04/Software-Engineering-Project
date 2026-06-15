# Spring Boot Backend MVP

> Language: [中文](README.md) | English

This directory contains a Maven + Spring Boot 3 + Java 17 backend MVP. Data is stored in memory so it can compile and run without database setup; `src/main/resources/db/init.sql` provides a future relational schema reference.

## Run

```bash
mvn test
mvn package
mvn spring-boot:run
```

The application listens on port `8080` by default.

## Default accounts

Seeded accounts use BCrypt password hashes in memory:

| Username | Password | Role |
| --- | --- | --- |
| `admin` | `Admin@123456` | `ADMIN` |
| `teacher` | `Teacher@123456` | `TEACHER` |
| `captain` | `Captain@123456` | `CAPTAIN` |

New self-registered users receive the `TRAINEE` role.

## Main APIs

All responses use the unified shape:

```json
{"code":0,"message":"ok","data":{},"timestamp":"2026-01-01T00:00:00Z"}
```

Implemented MVP endpoints:

- `GET /api/health`
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me` with `Authorization: Bearer <token>`
- `GET/POST/PATCH/DELETE /api/users`
- `GET/POST/PUT/DELETE /api/competitions`
- `GET /api/competitions/challenges`
- `POST /api/competitions/{competitionId}/challenges`
- `GET/POST /api/writeups?category=web`
- `GET/POST /api/training/tasks`
- `POST /api/training/tasks/{taskId}/submissions` with bearer token
- `GET /api/training/submissions`
- `GET/POST /api/notifications`
- `GET /api/dashboard`

The token format is JWT-like (`payload.signature`) and signed with HMAC-SHA256. For production, replace the default `app.token-secret`, introduce authorization checks per role, and persist data with database-backed repositories.
