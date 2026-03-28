package com.peoplestrong.NotificationSystem.Service;

import com.peoplestrong.NotificationSystem.DTO.Request.NotificationRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.NotificationResponseDto;
import jakarta.servlet.http.HttpSession;

public interface NotificationService {
    void send(NotificationRequestDto req, HttpSession session);
    java.util.List<com.peoplestrong.NotificationSystem.DTO.Response.SentNotificationResponseDto> getSentHistory(HttpSession session);
}
