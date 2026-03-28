package com.peoplestrong.NotificationSystem.Service;

import com.peoplestrong.NotificationSystem.DTO.Request.LoginRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.LoginResponseDto;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request, HttpSession session);
}
