package com.peoplestrong.NotificationSystem.Entity;

import com.peoplestrong.NotificationSystem.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="notification_targets")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="notification_id")
    private Long notificationId;

    private String targetType;

    @Column(name="user_id")
    private Long userId;

    @Column(name="role_id")
    private Long roleId;

    @Column(name="department_id")
    private Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="notification_id",insertable = false,updatable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",insertable = false,updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id",insertable = false,updatable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="department_id",insertable = false,updatable = false)
    private Department department;

}
