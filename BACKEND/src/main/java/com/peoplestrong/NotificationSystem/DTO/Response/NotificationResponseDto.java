package com.peoplestrong.NotificationSystem.DTO.Response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long userNotificationId;
    private Long notificationId;
    private Long userId;

    private String title;
    private String message;

    private String type;
    private String priority;

    private Boolean isRead;
    private String status;
    private LocalDateTime receivedAt;

    private String senderName;
    private String senderDepartment;
}
