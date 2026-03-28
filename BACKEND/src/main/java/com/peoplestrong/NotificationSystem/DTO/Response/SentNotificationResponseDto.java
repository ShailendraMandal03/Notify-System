package com.peoplestrong.NotificationSystem.DTO.Response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentNotificationResponseDto {
    private Long notificationId;
    private String title;
    private String message;
    private String type;
    private String priority;
    private LocalDateTime createdAt;
    
    private String targetType;
    private List<String> targetNames; // User names or Department name
    private List<Long> targetIds;
}
