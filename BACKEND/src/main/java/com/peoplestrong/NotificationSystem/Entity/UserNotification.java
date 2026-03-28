package com.peoplestrong.NotificationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user_notifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="notification_id")
    private Long notificationId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String type;
    private String priority;

    @Builder.Default
    @Column(name="is_read")
    private Boolean isRead=false;

    @Column(name="read_at")
    private LocalDateTime readAt;

    private String status;

    @Column(name = "received_at")
    private LocalDateTime receiveAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",insertable = false,updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="notification_id",insertable = false,updatable = false)
    private Notification notification;

}
