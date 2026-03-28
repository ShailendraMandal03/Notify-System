package com.peoplestrong.NotificationSystem.Service.imp;

import com.peoplestrong.NotificationSystem.DTO.Request.NotificationActionRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.LoginResponseDto;
import com.peoplestrong.NotificationSystem.DTO.Response.NotificationResponseDto;
import com.peoplestrong.NotificationSystem.Entity.NotificationAction;
import com.peoplestrong.NotificationSystem.Entity.User;
import com.peoplestrong.NotificationSystem.Entity.UserNotification;
import com.peoplestrong.NotificationSystem.Repository.NotificationActionRepository;
import com.peoplestrong.NotificationSystem.Repository.UserNotificationRepository;
import com.peoplestrong.NotificationSystem.Repository.UserRepository;
import com.peoplestrong.NotificationSystem.Service.UserNotificationService;
import com.peoplestrong.NotificationSystem.enums.ActionType;
import com.peoplestrong.NotificationSystem.enums.NotificationStatus;
import com.peoplestrong.NotificationSystem.exception.ResourceNotFoundException;
import com.peoplestrong.NotificationSystem.exception.SessionExpiredException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserNotificationServiceImp implements UserNotificationService {
    private final UserRepository userRepo;

    private final UserNotificationRepository userNotificationRepo;

    private final NotificationActionRepository notificationActionRepo;


    private Long currentUserId(HttpSession session)
    {

        LoginResponseDto logged=(LoginResponseDto) session.getAttribute("LOGIN_USER");
        if(logged==null)
        {
            throw new SessionExpiredException("Session Expired. Please login again");
        }
        return logged.getUserId();
    }

    @Override
    public void takeAction(Long notificationId, NotificationActionRequestDto req, HttpSession session) {
        try {
            Long userId = currentUserId(session);
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

            UserNotification n = userNotificationRepo.findById(notificationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

            if (!n.getUserId().equals(userId)) {
                throw new RuntimeException("Access Denied: You do not have permission to act on this notification");
            }

            if (req.getActionType() == ActionType.APPROVED) {
                n.setStatus(NotificationStatus.APPROVED.name());
            } else {
                n.setStatus(NotificationStatus.REJECTED.name());
            }

            userNotificationRepo.save(n);

            notificationActionRepo.save(
                    NotificationAction.builder()
                            .userNotificationId(n.getId())
                            .actionType(req.getActionType().name())
                            .actionBy(user.getId())
                            .actionAt(LocalDateTime.now())
                            .build()
            );
            log.info("Action Saved successfully for ID: {}", notificationId);
        } catch (ResourceNotFoundException | SessionExpiredException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in takeAction for notification {}: {}", notificationId, e.getMessage(), e);
            throw new RuntimeException("Action failed for notification " + notificationId, e);
        } finally {
            log.debug("Finished takeAction attempt for {}", notificationId);
        }
    }

    @Override
    public List<NotificationResponseDto> getUserNotification(HttpSession session) {
        try {
            Long userId = currentUserId(session);
            List<UserNotification> notify = userNotificationRepo.findByUserIdOrderByReceiveAtDesc(userId);

            List<NotificationResponseDto> responseList = new ArrayList<>();

            for (UserNotification n : notify) {
                NotificationResponseDto dto = new NotificationResponseDto().builder()
                        .userNotificationId(n.getId())
                        .notificationId(n.getNotificationId())
                        .userId(n.getUserId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .type(n.getType() != null ? n.getType() : null)
                        .priority(n.getPriority() != null ? n.getPriority() : null)
                        .isRead(n.getIsRead())
                        .status(n.getStatus() != null ? n.getStatus() : null)
                        .receivedAt(n.getReceiveAt())
                        .senderName(n.getNotification() != null && n.getNotification().getCreator() != null ? n.getNotification().getCreator().getName() : "System")
                        .senderDepartment(n.getNotification() != null && n.getNotification().getCreator() != null && n.getNotification().getCreator().getDepartment() != null
                                ? n.getNotification().getCreator().getDepartment().getDepartment_name() : "Department")
                        .build();

                responseList.add(dto);
            }

            return responseList;
        } catch (Exception e) {
            log.error("Error in getUserNotification: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.debug("Notification retrieval completed");
        }
    }

    @Override
    public void markAsRead(Long notificationId, HttpSession session) {
        try {
            Long userId = currentUserId(session);
            UserNotification n = userNotificationRepo.findById(notificationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));


            if (!n.getUserId().equals(userId)) {
                throw new RuntimeException("Access Denied");
            }

            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
            userNotificationRepo.save(n);
        } catch (Exception e) {
            log.error("Error in markAsRead for {}: {}", notificationId, e.getMessage(), e);
            throw e;
        } finally {
            log.debug("markAsRead completed for {}", notificationId);
        }
    }

    @Override
    public int markAllasread(HttpSession session) {
        try {
            Long userId = currentUserId(session);
            userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

            return userNotificationRepo.markAllRead(userId);
        } catch (Exception e) {
            log.error("Error in markAllasread: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.debug("markAllasread completed");
        }
    }

    @Override
    public Long getUnreadCount(HttpSession session) {
        Long userId=currentUserId(session);
        return userNotificationRepo.countByUserIdAndIsReadFalse(userId);
    }
}
