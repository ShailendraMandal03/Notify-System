import { Injectable, NgZone } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { NotificationService } from './notification.service';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

  private client: Client | null = null;
  private connectedUserId: number | null = null;

  constructor(
    private notificationService: NotificationService,
    private zone: NgZone
  ) {}

  connect(userId: number) {
    if (this.client?.active && this.connectedUserId === userId) {
      console.log('✔ WebSocket already active for user ' + userId);
      return;
    }

    if (this.client?.active) {
      this.client.deactivate();
    }

    const client = new Client({
      webSocketFactory: () =>
        new SockJS('http://127.0.0.1:8080/ws', undefined, {
          withCredentials: true
        }),
      reconnectDelay: 5000
    });

    client.onConnect = () => {
      console.log('✔ WebSocket connected as user ' + userId);
      this.connectedUserId = userId;
      client.subscribe(`/topic/notification/${userId}`, message => {
        this.zone.run(() => {
          const notif = JSON.parse(message.body);
          this.notificationService.emitNewNotification(notif);
        });
      });
    };

    this.client = client;
    client.activate();
  }
}