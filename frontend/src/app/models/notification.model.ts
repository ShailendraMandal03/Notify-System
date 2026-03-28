export enum NotificationType {
  INFO = 'INFO',
  SUCCESS = 'SUCCESS',
  WARNING = 'WARNING',
  ERROR = 'ERROR',
  ACTION_REQUIRED = 'ACTION_REQUIRED',
  APPROVED_REQUEST = 'APPROVED_REQUEST',
  REMINDER = 'REMINDER',
  DEADLINE = 'DEADLINE',
  EVENT = 'EVENT'
}

export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export enum TargetType {
  ALL = 'ALL',
  USER = 'USER',
  DEPARTMENT = 'DEPARTMENT'
}

export enum ActionType {
  APPROVED = 'APPROVED',
  REJECT = 'REJECT'
}

export interface NotificationRequestDto {
  title: string;
  message: string;
  type: NotificationType;
  priority: Priority;
  targetType: TargetType;
  userIds?: number[];
  rolesId?: number;
  departmentId?: number;
}

export interface NotificationModel {
  userNotificationId: number;
  notificationId: number;
  userId: number;
  title: string;
  message: string;
  type: NotificationType;
  priority: Priority;
  isRead: boolean;
  status: string;
  receivedAt: string;
  senderName?: string;
  senderDepartment?: string;
}

export interface SentNotificationModel {
  notificationId: number;
  title: string;
  message: string;
  type: NotificationType;
  priority: Priority;
  createdAt: string;
  targetType: TargetType;
  targetNames: string[];
  targetIds: number[];
}