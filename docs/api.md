# API 草案

所有接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 认证

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| GET | `/api/auth/me` | 当前用户 |

## 用户管理

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/users` | 用户列表 |
| POST | `/api/admin/users` | 创建用户 |
| PUT | `/api/admin/users/{id}` | 修改用户 |
| PUT | `/api/admin/users/{id}/status` | 修改状态 |
| PUT | `/api/admin/users/{id}/role` | 修改角色 |
| DELETE | `/api/admin/users/{id}` | 删除用户 |

## 赛事与赛题

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/competitions` | 比赛列表 |
| POST | `/api/competitions` | 创建比赛 |
| GET | `/api/competitions/{id}` | 比赛详情 |
| PUT | `/api/competitions/{id}` | 修改比赛 |
| DELETE | `/api/competitions/{id}` | 删除比赛 |
| GET | `/api/competitions/{id}/problems` | 赛题列表 |
| POST | `/api/competitions/{id}/problems` | 添加赛题 |
| PUT | `/api/problems/{id}` | 修改赛题 |
| PUT | `/api/problems/{id}/claim` | 认领赛题 |
| PUT | `/api/problems/{id}/status` | 修改赛题状态 |

## 知识库、训练、通知、看板

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET/POST | `/api/writeups` | WriteUp 列表/创建 |
| GET/PUT/DELETE | `/api/writeups/{id}` | WriteUp 详情/修改/删除 |
| GET/POST | `/api/training/tasks` | 训练任务列表/创建 |
| POST | `/api/training/tasks/{id}/submissions` | 提交训练 |
| PUT | `/api/training/submissions/{id}/score` | 训练评分 |
| GET/POST | `/api/notifications` | 通知列表/发布 |
| PUT | `/api/notifications/{id}/read` | 标记已读 |
| GET | `/api/dashboard/overview` | 看板总览 |
| GET | `/api/dashboard/trend` | 趋势数据 |
| GET | `/api/dashboard/distribution` | 分布数据 |
