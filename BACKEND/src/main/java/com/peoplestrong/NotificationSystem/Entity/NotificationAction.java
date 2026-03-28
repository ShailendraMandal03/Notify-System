package com.peoplestrong.NotificationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="notification_action")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_notification_id")
    private Long userNotificationId;

    @Column(name="action_type")
    private String actionType;

    @Column(name="action_by")
    private Long actionBy;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_notification_id",insertable = false,updatable = false)
    private UserNotification userNotification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="action_by",insertable = false,updatable = false)
    private User actionUser;

}
