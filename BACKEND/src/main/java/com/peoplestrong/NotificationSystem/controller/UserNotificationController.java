package com.peoplestrong.NotificationSystem.controller;

import com.peoplestrong.NotificationSystem.DTO.Request.NotificationActionRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.NotificationResponseDto;
import com.peoplestrong.NotificationSystem.Service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Dashboard", description = "Endpoints for managing received notifications and unread counts")
public class UserNotificationController {
    private final UserNotificationService userNotificationService;

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Retrieves the full list of notifications for the logged-in user")
    public List<NotificationResponseDto> getALL(HttpSession session)
    {
        try {
            log.debug("Fetching all notifications for user");
            return userNotificationService.getUserNotification(session);
        } catch (Exception e) {
            log.error("UserNotification Controller Error (getALL): {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Marks a specific notification as read for the current user")
    public ResponseEntity<String> markAsRead(@PathVariable("id") Long id, HttpSession session)
    {
        try {
            log.info("Marking notification {} as read", id);
            userNotificationService.markAsRead(id, session);
            return ResponseEntity.ok("Marked as read");
        } catch (Exception e) {
            log.error("UserNotification Controller Error (markAsRead) for ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Marks all unread notifications as read for the current user")
    public ResponseEntity<String> markAllAsRead(HttpSession session)
    {
        try {
//        int updated=userNotificationService.markAllasread(session);
            log.info("Marking all notifications as read");
            userNotificationService.markAllasread(session);
            return ResponseEntity.ok("All marked as read");
        } catch (Exception e) {
            log.error("UserNotification Controller Error (markAllAsRead): {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Returns the count of unread notifications for the user")
    public Long unReadCount(HttpSession session)
    {
        try {
            return userNotificationService.getUnreadCount(session);
        } catch (Exception e) {
            log.error("UserNotification Controller Error (unread-count): {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/{id}/action")
    @Operation(summary = "Take Action", description = "Saves the user choice (Approve/Reject) on an actionable notification")
    public ResponseEntity<String> action(@PathVariable("id") Long id,
                         @Valid @RequestBody NotificationActionRequestDto req,
                         HttpSession session)
    {
        try {
            log.info("User taking action {} on notification {}", req.getActionType(), id);
            userNotificationService.takeAction(id, req, session);
            return ResponseEntity.ok("Action Saved");
        } catch (Exception e) {
            log.error("UserNotification Controller Error (action) for ID {}: {}", id, e.getMessage());
            throw e;
        }
    }


}
