import type { DashboardStats, EventItem, NotificationItem, TrainingTask, User, WriteUp } from './types'

export const mockUsers: User[] = [
  { id: 1, username: 'admin', nickname: '平台管理员', role: 'admin', email: 'admin@example.com', status: 'active', lastLogin: '2026-06-10 21:30' },
  { id: 2, username: 'coach_lin', nickname: '林教练', role: 'TEACHER', email: 'coach@example.com', status: 'active', lastLogin: '2026-06-09 09:12' },
  { id: 3, username: 'player_chen', nickname: '陈同学', role: 'MEMBER', email: 'player@example.com', status: 'disabled', lastLogin: '2026-06-01 18:45' },
]

export const mockEvents: EventItem[] = [
  {
    id: 101,
    title: '校内 CTF 夏季赛',
    status: 'running',
    startTime: '2026-06-08 09:00',
    endTime: '2026-06-12 18:00',
    format: 'Jeopardy',
    organizer: '网络安全协会',
    description: '面向校内成员的综合训练赛，覆盖 Web、Crypto、Pwn、Reverse、Misc。',
    challenges: [
      { id: 1, title: 'Easy Login', category: 'Web', score: 100, solved: 42 },
      { id: 2, title: 'Baby RSA', category: 'Crypto', score: 150, solved: 23 },
      { id: 3, title: 'Stack Warmup', category: 'Pwn', score: 200, solved: 11 },
    ],
  },
  {
    id: 102,
    title: '蓝桥杯网络安全模拟赛',
    status: 'upcoming',
    startTime: '2026-06-20 10:00',
    endTime: '2026-06-20 18:00',
    format: '个人赛',
    organizer: '训练中心',
    description: '赛前能力摸底，自动同步报名名单和赛题进度。',
    challenges: [{ id: 4, title: 'Forensics 101', category: 'Misc', score: 100, solved: 0 }],
  },
]

export const mockWriteUps: WriteUp[] = [
  {
    id: 201,
    title: 'Easy Login 解题记录',
    eventTitle: '校内 CTF 夏季赛',
    challengeTitle: 'Easy Login',
    author: '林教练',
    visibility: 'team',
    updatedAt: '2026-06-10 22:15',
    tags: ['Web', 'SQLi'],
    content: '# 思路\n\n通过抓包发现登录接口存在注入点，使用布尔盲注获取管理员密码。\n\n## 复盘\n\n需要补充 WAF 绕过用例。',
  },
  {
    id: 202,
    title: 'Baby RSA 快速复盘',
    eventTitle: '校内 CTF 夏季赛',
    challengeTitle: 'Baby RSA',
    author: '陈同学',
    visibility: 'public',
    updatedAt: '2026-06-09 17:40',
    tags: ['Crypto', 'RSA'],
    content: '# 关键点\n\n低指数广播攻击，使用 CRT 合并后开三次方。',
  },
]

export const mockTrainingTasks: TrainingTask[] = [
  { id: 301, title: '完成 SQL 注入靶场', category: 'Web', difficulty: '入门', status: 'doing', dueDate: '2026-06-15', assignee: '陈同学', progress: 60 },
  { id: 302, title: 'Pwn 栈溢出专题', category: 'Pwn', difficulty: '进阶', status: 'todo', dueDate: '2026-06-22', assignee: '训练一队', progress: 10 },
  { id: 303, title: 'RSA 常见攻击总结', category: 'Crypto', difficulty: '入门', status: 'done', dueDate: '2026-06-05', assignee: '密码组', progress: 100 },
]

export const mockNotifications: NotificationItem[] = [
  { id: 401, title: '夏季赛即将结束', type: 'event', read: false, createdAt: '2026-06-11 08:00', content: '校内 CTF 夏季赛将在 6 月 12 日 18:00 结束，请及时提交 WriteUp。' },
  { id: 402, title: '训练任务更新', type: 'training', read: true, createdAt: '2026-06-10 19:20', content: 'SQL 注入靶场新增 3 道练习题。' },
]

export const mockDashboard: DashboardStats = {
  users: 86,
  runningEvents: 1,
  writeups: 28,
  pendingTasks: 12,
  categoryScores: [
    { category: 'Web', score: 86 },
    { category: 'Crypto', score: 68 },
    { category: 'Pwn', score: 51 },
    { category: 'Reverse', score: 59 },
    { category: 'Misc', score: 73 },
  ],
  recentActivities: [
    { id: 1, action: '林教练发布了 Easy Login WriteUp', time: '10 分钟前' },
    { id: 2, action: '陈同学完成 SQL 注入靶场 60%', time: '1 小时前' },
    { id: 3, action: '校内 CTF 夏季赛新增 1 道 Pwn 题', time: '昨天' },
  ],
}
