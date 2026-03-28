package com.peoplestrong.NotificationSystem.Service;

import com.peoplestrong.NotificationSystem.DTO.Request.NotificationActionRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.NotificationResponseDto;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface UserNotificationService {

    void takeAction(Long notificationId, NotificationActionRequestDto req, HttpSession session);

    List<NotificationResponseDto>getUserNotification(HttpSession session);

    void markAsRead(Long notificationId, HttpSession session);

    int markAllasread(HttpSession session);

    Long getUnreadCount(HttpSession session);

}
