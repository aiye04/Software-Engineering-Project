# 数据库说明

MVP 阶段数据库脚本位于：`backend/src/main/resources/db/init.sql`。

## 核心表

| 表名 | 说明 |
| --- | --- |
| `sys_user` | 用户、角色、状态、密码摘要 |
| `competition` | 比赛基础信息 |
| `competition_problem` | 赛题、分值、状态、认领人 |
| `cooperation_log` | 协同消息记录 |
| `writeup_category` | WriteUp 分类 |
| `writeup` | WriteUp 内容 |
| `writeup_tag` | 标签关联 |
| `training_task` | 训练任务 |
| `training_submission` | 训练提交 |
| `notification` | 通知公告 |
| `notification_read` | 通知已读 |
| `operation_log` | 操作日志 |

## 说明

当前后端 MVP 可以先使用内存数据服务保证课程演示闭环；SQL 脚本用于后续切换 MySQL/JPA 或 MyBatis 时落库。
