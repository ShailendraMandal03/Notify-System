import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../services/notification.service';
import { WebSocketService } from '../../services/websocket.service';
import { NotificationModel, ActionType, NotificationType } from '../../models/notification.model';

@Component({
  standalone: true,
  selector: 'app-notification-bell',
  imports: [CommonModule],
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.css']
})
export class NotificationBellComponent implements OnInit {

  notifications: NotificationModel[] = [];
  unread = 0;
  userId!: number;
  isOpen = false;
  ActionType = ActionType;
  private refreshSub?: Subscription;

  constructor(
    private notif: NotificationService,
    private ws: WebSocketService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const user = JSON.parse(localStorage.getItem('user')!);
    this.userId = user.userId;

    this.loadNotifications();
    this.loadUnread();

    // 🔥 Real-time
    this.ws.connect(this.userId);
    this.notif.notifications$.subscribe(msg => {
      this.notifications.unshift(msg);
      this.unread++;
      this.cdr.detectChanges();
    });

    // 🔄 Sync on state changes (read/unread)
    this.refreshSub = this.notif.refreshNotifications$.subscribe(() => {
      setTimeout(() => {
        this.loadUnread();
        this.loadNotifications();
      }, 300);
    });
  }

  loadNotifications() {
    this.notif.getAll()
      .subscribe(res => {
        this.notifications = res;
        this.cdr.detectChanges();
      });
  }

  loadUnread() {
    this.notif.unreadCount()
      .subscribe(count => {
        this.unread = count;
        this.cdr.detectChanges();
      });
  }

  markRead(n: NotificationModel) {
    if (!n.isRead) {
      this.notif.markRead(n.userNotificationId)
        .subscribe(() => {
          n.isRead = true;
          this.unread = Math.max(0, this.unread - 1);
          this.cdr.detectChanges();
        });
    }
    // Always emit the open event when clicking, even if already read
    this.notif.openNotification(n);
    this.isOpen = false; // Close the dropdown after clicking
  }

  markAll() {
    this.notif.markAll()
      .subscribe(() => {
        this.notifications.forEach(n => n.isRead = true);
        this.unread = 0;
        this.cdr.detectChanges();
      });
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  handleAction(event: Event, n: NotificationModel, action: ActionType) {
    event.stopPropagation(); // Don't trigger markRead
    this.notif.takeAction(n.userNotificationId, action).subscribe({
      next: () => {
        n.status = action === ActionType.APPROVED ? 'APPROVED' : 'REJECTED';
        n.isRead = true;
        this.unread = Math.max(0, this.unread - 1);
        this.cdr.detectChanges();
        alert(`Notification ${action.toLowerCase()} success`);
      },
      error: (err) => alert('Action failed: ' + err.message)
    });
  }

  isActionRequired(n: NotificationModel): boolean {
    return n.type === NotificationType.ACTION_REQUIRED && n.status === 'PENDING';
  }
}