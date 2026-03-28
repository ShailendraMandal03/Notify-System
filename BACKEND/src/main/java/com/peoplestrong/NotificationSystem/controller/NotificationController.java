package com.peoplestrong.NotificationSystem.controller;


import com.peoplestrong.NotificationSystem.DTO.Request.NotificationRequestDto;
import com.peoplestrong.NotificationSystem.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notifications")
@Slf4j
@Tag(name = "Admin Notifications", description = "Internal APIs for broadcasting and managing sent history")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Broadcast Notification", description = "Sends a real-time notification to specified targets (ALL, USER, or DEPARTMENT)")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationRequestDto request, HttpSession session)
    {
        try {
            notificationService.send(request, session);
            log.info("Notification sent successfully: {}", request.getTitle());
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            log.error("Notification Controller Error (send) for {}: {}", request.getTitle(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Get Sent History", description = "Retrieves a detailed history of all notifications sent by the current admin")
    public ResponseEntity<java.util.List<com.peoplestrong.NotificationSystem.DTO.Response.SentNotificationResponseDto>> getHistory(HttpSession session) {
        try {
            log.info("Fetching sent history");
            return ResponseEntity.ok(notificationService.getSentHistory(session));
        } catch (Exception e) {
            log.error("Notification Controller Error (history): {}", e.getMessage());
            throw e;
        }
    }
}
