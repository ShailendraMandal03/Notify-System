import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject, tap } from 'rxjs';
import { NotificationModel, NotificationRequestDto, ActionType, SentNotificationModel } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private base = 'http://127.0.0.1:8080/api/user/notifications';
  private adminBase = 'http://127.0.0.1:8080/api/admin/notifications';

  private notificationStream = new Subject<NotificationModel>();
  public notifications$ = this.notificationStream.asObservable();

  // Observable for state refreshes (read/unread changes)
  private refreshSubject = new Subject<void>();
  public refreshNotifications$ = this.refreshSubject.asObservable();

  // Observable for opening notification detail from other components
  private openNotifSubject = new Subject<NotificationModel>();
  public openNotification$ = this.openNotifSubject.asObservable();

  constructor(private http: HttpClient) {}

  triggerRefresh() {
    this.refreshSubject.next();
  }

  openNotification(notification: NotificationModel) {
    this.openNotifSubject.next(notification);
  }

  emitNewNotification(notification: NotificationModel) {
    this.notificationStream.next(notification);
  }

  getAll() {
    return this.http.get<NotificationModel[]>(
      this.base,
      {withCredentials:true}      
    );
  }

  markRead(id: number) {
    return this.http.put(
      `${this.base}/${id}/read`,
      {},
      {withCredentials:true, responseType: 'text'}      
    ).pipe(tap(() => this.triggerRefresh()));
  }

  markAll() {
    return this.http.put(
      `${this.base}/read-all`, 
      {},
      {withCredentials:true, responseType: 'text'}
    ).pipe(tap(() => this.triggerRefresh()));
  }

  unreadCount() {
    return this.http.get<number>(
      `${this.base}/unread-count`,
      {withCredentials:true}
    );
  }

  takeAction(id: number, actionType: ActionType) {
    return this.http.post(
      `${this.base}/${id}/action`,
      { actionType },
      {withCredentials:true, responseType: 'text'}
    );
  }

  sendNotification(data: NotificationRequestDto) {
    return this.http.post(
      this.adminBase,
      data,
      {withCredentials:true, responseType: 'text'}
    );
  }

  getSentHistory() {
    return this.http.get<SentNotificationModel[]>(
      `${this.adminBase}/history`,
      {withCredentials:true}
    );
  }
}