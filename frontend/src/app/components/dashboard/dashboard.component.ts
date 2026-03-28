import { Component, OnInit, OnDestroy, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, Subject, debounceTime, distinctUntilChanged, switchMap, of, catchError } from 'rxjs';
import { NotificationBellComponent } from '../notification-bell/notification-bell.component';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { MetadataService, UserMetadata, DepartmentMetadata } from '../../services/metadata.service';
import { 
  NotificationRequestDto, 
  NotificationType, 
  Priority, 
  TargetType,
  ActionType,
  NotificationModel,
  SentNotificationModel 
} from '../../models/notification.model';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule, NotificationBellComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  isAdmin = false;
  user: any;
  activeView: 'analytics' | 'compose' | 'history' | 'all-notifications' = 'analytics';
  activeFilter: 'ALL' | 'SYSTEM' | 'ALERTS' | 'UNREAD' | 'READ' = 'ALL';
  isSidebarCollapsed = false;

  // Stats
  totalReceived = 0;
  readCount = 0;
  unreadCount = 0;

  notifications: NotificationModel[] = [];
  filteredNotifications: NotificationModel[] = [];
  sentHistory: SentNotificationModel[] = [];

  // Real-time toast
  latestNotification: NotificationModel | null = null;
  showToast = false;
  private sub?: Subscription;
  
  // Side Panel state
  selectedNotif: NotificationModel | null = null;
  showDetailSide = false;

  // Metadata & Audience Search
  departments: DepartmentMetadata[] = [];
  userSearchResults: UserMetadata[] = [];
  selectedUsers: UserMetadata[] = []; // Changed to array
  userSearchSubject = new Subject<string>();
  isSearching = false;

  notification: NotificationRequestDto = {
    title: '',
    message: '',
    type: NotificationType.INFO,
    priority: Priority.MEDIUM,
    targetType: TargetType.ALL,
    userIds: [],
    rolesId: undefined,
    departmentId: undefined
  };

  // Enum access for template
  NotificationType = NotificationType;
  Priority = Priority;
  TargetType = TargetType;

  types = Object.values(NotificationType);
  priorities = Object.values(Priority);
  targets = Object.values(TargetType);
  ActionType = ActionType;

  constructor(
    private auth: AuthService,
    private notificationService: NotificationService,
    private metadataService: MetadataService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    this.user = this.auth.getUser();
    if (!this.user) {
      this.router.navigate(['/']);
      return;
    }
    this.isAdmin = this.user?.role_name === 'ADMIN';
    
    // Set default view based on role
    this.activeView = this.isAdmin ? 'compose' : 'analytics';

    // Auto-collapse sidebar on small windows
    this.isSidebarCollapsed = window.innerWidth < 768;
    window.addEventListener('resize', () => {
      this.isSidebarCollapsed = window.innerWidth < 768;
      this.cdr.detectChanges();
    });

    this.loadData();
    this.loadMetadata();

    // 🔍 Setup Debounced User Search
    this.userSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (term.length < 2) return of([]);
        this.isSearching = true;
        return this.metadataService.searchUsers(term).pipe(
          catchError(() => of([]))
        );
      })
    ).subscribe(results => {
      console.log('Search results received:', results);
      this.ngZone.run(() => {
        this.userSearchResults = results;
        this.isSearching = false;
        this.cdr.detectChanges();
        console.log('userSearchResults updated, length:', this.userSearchResults.length);
      });
    });

    // Listen for real-time notifications
    this.sub = this.notificationService.notifications$.subscribe(notif => {
      this.ngZone.run(() => {
        this.notifications = [notif, ...this.notifications];
        this.updateCounts();
        this.applyFilter();
        
        this.latestNotification = notif;
        this.showToast = true;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.showToast = false;
          this.cdr.detectChanges();
        }, 5000);
      });
    });

    // 🛰️ Listen for external open requests (e.g. from Bell)
    this.sub?.add(this.notificationService.openNotification$.subscribe(notif => {
      this.ngZone.run(() => {
        // Switch to notifications view if not already there
        this.activeView = 'analytics';
        // Give UI a tiny moment to switch tab before opening detail
        setTimeout(() => {
          this.openDetail(notif);
        }, 100);
      });
    }));

    // 🔄 Sync on state changes (read/unread) with a tiny delay to ensure DB commit is ready
    this.sub?.add(this.notificationService.refreshNotifications$.subscribe(() => {
      setTimeout(() => {
        this.ngZone.run(() => {
          this.loadData();
        });
      }, 300);
    }));
  }

  loadData() {
    this.notificationService.getAll().subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.notifications = res;
          this.updateCounts();
          this.applyFilter();
          this.cdr.detectChanges();
        });
      },
      error: (err) => console.error('Failed to load data', err)
    });
  }

  updateCounts() {
    this.totalReceived = this.notifications.length;
    this.unreadCount = this.notifications.filter(n => !n.isRead).length;
    this.readCount = this.totalReceived - this.unreadCount;
  }

  applyFilter() {
    const all = [...this.notifications];
    if (this.activeFilter === 'ALL') {
      this.filteredNotifications = all;
    } else if (this.activeFilter === 'SYSTEM') {
      this.filteredNotifications = all.filter(n => 
        n.type === NotificationType.INFO || 
        n.type === NotificationType.SUCCESS ||
        n.type === NotificationType.APPROVED_REQUEST
      );
    } else if (this.activeFilter === 'ALERTS') {
      // ALERTS including WARNING, ERROR, ACTION_REQUIRED, REMINDER, DEADLINE
      this.filteredNotifications = all.filter(n => 
        n.type === NotificationType.ERROR || 
        n.type === NotificationType.WARNING || 
        n.type === NotificationType.ACTION_REQUIRED ||
        n.type === NotificationType.REMINDER ||
        n.type === NotificationType.DEADLINE ||
        n.type === NotificationType.EVENT
      );
    } else if (this.activeFilter === 'UNREAD') {
      this.filteredNotifications = all.filter(n => !n.isRead);
    } else if (this.activeFilter === 'READ') {
      this.filteredNotifications = all.filter(n => n.isRead);
    }
  }

  setFilter(f: 'ALL' | 'SYSTEM' | 'ALERTS' | 'UNREAD' | 'READ') {
    this.activeFilter = f;
    this.applyFilter();
  }

  setView(v: 'analytics' | 'compose' | 'history' | 'all-notifications') {
    this.activeView = v;
    if (v === 'history' && this.isAdmin) {
      this.loadHistory();
    }
    // Reset filter to ALL when switching to all-notifications
    if (v === 'all-notifications') {
      this.activeFilter = 'ALL';
      this.applyFilter();
    }
  }

  loadMetadata() {
    this.metadataService.getDepartments().subscribe({
      next: (depts) => {
        this.departments = depts;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load departments', err)
    });
  }

  loadHistory() {
    this.notificationService.getSentHistory().subscribe({
      next: (data) => {
        this.sentHistory = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load history', err)
    });
  }

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  logout() {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/']);
    });
  }

  markAllAsRead() {
    this.notificationService.markAll().subscribe(() => {
      this.notifications.forEach(n => n.isRead = true);
      this.updateCounts();
      this.cdr.detectChanges();
    });
  }

  markAllAsReadAndView() {
    this.notificationService.markAll().subscribe(() => {
      this.notifications.forEach(n => n.isRead = true);
      this.updateCounts();
      this.setView('all-notifications');
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  pushNotification() {
    // 🛑 Validation for selective targeting
    if (this.notification.targetType === TargetType.USER && this.selectedUsers.length === 0) {
        alert('Please search and select at least one user');
        return;
    }
    if (this.notification.targetType === TargetType.DEPARTMENT && !this.notification.departmentId) {
        alert('Please select a department from the list');
        return;
    }

    // Preparation for send: Ensure selected user IDs are in userIds list
    if (this.notification.targetType === TargetType.USER) {
        this.notification.userIds = this.selectedUsers.map(u => u.id);
    }

    this.notificationService.sendNotification(this.notification).subscribe({
      next: (res) => {
        alert('Notification pushed successfully!');
        this.resetForm();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to push notification');
      }
    });
  }

  markRead(n: NotificationModel) {
    if (!n.isRead) {
      // Optimistic update for instant "automatic" feel
      n.isRead = true;
      this.updateCounts();
      this.applyFilter();
      this.cdr.detectChanges();

      this.notificationService.markRead(n.userNotificationId).subscribe({
        next: () => {
          // Sync successful, already updated locally
          console.log('Marked as read on server');
        },
        error: (err) => {
          // Rollback on error if server processing failed
          console.error('Mark as read failed on server', err);
          this.ngZone.run(() => {
            n.isRead = false;
            this.updateCounts();
            this.applyFilter();
            this.cdr.detectChanges();
          });
        }
      });
    }
  }

  isActionRequired(n: NotificationModel): boolean {
    return n.type === NotificationType.ACTION_REQUIRED && n.status === 'PENDING';
  }

  openDetail(n: NotificationModel) {
    this.selectedNotif = n;
    this.showDetailSide = true;
    
    // Automatically mark as read if it's currently unread
    if (!n.isRead) {
      this.markRead(n);
    }
    this.cdr.detectChanges();
  }

  closeDetail() {
    this.showDetailSide = false;
    this.selectedNotif = null;
    this.cdr.detectChanges();
  }

  handleAction(n: NotificationModel, action: ActionType) {
    this.notificationService.takeAction(n.userNotificationId, action).subscribe({
      next: () => {
        n.status = action === ActionType.APPROVED ? 'APPROVED' : 'REJECTED';
        n.isRead = true;
        this.loadData(); // Refresh all counts and list
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Action failed', err);
        alert('Action failed: Request processing error on server.');
        this.cdr.detectChanges();
      }
    });
  }

  private resetForm() {
    this.notification = {
      title: '',
      message: '',
      type: NotificationType.INFO,
      priority: Priority.MEDIUM,
      targetType: TargetType.ALL,
      userIds: [],
      rolesId: undefined,
      departmentId: undefined
    };
    this.selectedUsers = []; // Clear array
    this.userSearchResults = [];
  }

  onUserSearch(event: any) {
    const term = event.target.value;
    this.userSearchSubject.next(term);
  }

  selectUser(user: UserMetadata) {
    // Check if user already added
    if (!this.selectedUsers.find(u => u.id === user.id)) {
        this.selectedUsers.push(user);
    }
    this.userSearchResults = [];
    // Update the notification object just in case
    this.notification.userIds = this.selectedUsers.map(u => u.id);
    this.cdr.detectChanges();
  }

  removeUser(user: UserMetadata) {
    this.selectedUsers = this.selectedUsers.filter(u => u.id !== user.id);
    this.notification.userIds = this.selectedUsers.map(u => u.id);
    this.cdr.detectChanges();
  }

  onDeptChange(event: any) {
    // Value from select is a string, convert to Number
    this.notification.departmentId = Number(event.target.value);
  }
}