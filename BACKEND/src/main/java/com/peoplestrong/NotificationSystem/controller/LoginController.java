package com.peoplestrong.NotificationSystem.controller;

import com.peoplestrong.NotificationSystem.DTO.Request.LoginRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.LoginResponseDto;
import com.peoplestrong.NotificationSystem.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user login and session management")
public class LoginController
{
    private final AuthService authservice;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and starts a session")
    public ResponseEntity<LoginResponseDto> Login(
            @RequestBody LoginRequestDto request, HttpSession session)
    {
        try {
            LoginResponseDto res = authservice.login(request, session);
            log.info("Login Success for user: {}", request.getEmail());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Login Controller Error for {}: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the current user session")
    public ResponseEntity<String> logout(HttpSession session) {
        try {
            session.invalidate();
            log.info("User logged out successfully");
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            log.error("Logout Error: {}", e.getMessage());
            throw e;
        }
    }
}
