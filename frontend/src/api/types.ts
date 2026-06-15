export interface ApiResult<T> {
  code?: number
  message?: string
  data: T
}

export type UserRole = 'ADMIN' | 'TEACHER' | 'CAPTAIN' | 'CORE' | 'MEMBER' | 'TRAINEE' | 'admin' | 'teacher' | 'captain' | 'core' | 'member' | 'trainee'

export interface User {
  id: number
  username: string
  nickname?: string
  displayName?: string
  role?: UserRole
  roles?: UserRole[]
  email?: string
  status?: 'active' | 'disabled'
  enabled?: boolean
  lastLogin?: string
}

export interface EventItem {
  id: number
  title: string
  status?: 'upcoming' | 'running' | 'ended'
  startTime: string
  endTime: string
  format?: string
  organizer?: string
  description: string
  challenges: Challenge[]
}

export interface Challenge {
  id: number
  title: string
  category: string
  score: number
  solved?: number
  status?: string
  claimedBy?: number
}

export interface WriteUp {
  id: number
  title: string
  eventTitle?: string
  challengeTitle?: string
  author?: string
  authorId?: number
  category?: string
  visibility?: 'public' | 'team' | 'private'
  updatedAt?: string
  createdAt?: string
  content: string
  tags?: string[]
}

export interface TrainingTask {
  id: number
  title: string
  description?: string
  category: string
  difficulty?: '入门' | '进阶' | '困难'
  status?: 'todo' | 'doing' | 'done'
  dueDate?: string
  dueAt?: string
  assignee?: string
  assigneeId?: number
  progress?: number
}

export interface NotificationItem {
  id: number
  title: string
  type?: 'system' | 'event' | 'training'
  level?: string
  read: boolean
  createdAt: string
  content: string
}

export interface DashboardStats {
  users?: number
  userCount?: number
  runningEvents?: number
  competitionCount?: number
  writeups?: number
  writeUpCount?: number
  pendingTasks?: number
  trainingTaskCount?: number
  categoryScores?: Array<{ category: string; score: number }>
  recentActivities?: Array<{ id: number; action: string; time: string }>
}
