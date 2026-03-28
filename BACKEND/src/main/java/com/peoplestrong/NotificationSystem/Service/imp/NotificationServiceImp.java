package com.peoplestrong.NotificationSystem.Service.imp;

import com.peoplestrong.NotificationSystem.DTO.Request.NotificationRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.LoginResponseDto;
import com.peoplestrong.NotificationSystem.DTO.Response.NotificationResponseDto;
import com.peoplestrong.NotificationSystem.Entity.Notification;
import com.peoplestrong.NotificationSystem.Entity.NotificationTarget;
import com.peoplestrong.NotificationSystem.Entity.User;
import com.peoplestrong.NotificationSystem.Entity.UserNotification;
import com.peoplestrong.NotificationSystem.Repository.NotificationRepository;
import com.peoplestrong.NotificationSystem.Repository.NotificationTargetRepository;
import com.peoplestrong.NotificationSystem.Repository.UserNotificationRepository;
import com.peoplestrong.NotificationSystem.Repository.UserRepository;
import com.peoplestrong.NotificationSystem.Service.NotificationService;
import com.peoplestrong.NotificationSystem.enums.NotificationStatus;
import com.peoplestrong.NotificationSystem.enums.NotificationType;
import com.peoplestrong.NotificationSystem.enums.TargetType;
import com.peoplestrong.NotificationSystem.exception.ResourceNotFoundException;
import com.peoplestrong.NotificationSystem.exception.SessionExpiredException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final NotificationTargetRepository notificationTargetRepo;
    private final UserNotificationRepository userNotificationRepo;

    private final UserRepository userRepo;

//    private final ModelMapper modelMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public void send(NotificationRequestDto req, HttpSession session) {
        try {
            log.info("Processing send request: {}", req.getTitle());
            Object userAttr = session.getAttribute("LOGIN_USER");

            if (userAttr == null) {
                throw new SessionExpiredException("Session expired. Please Login again.");
            }
            LoginResponseDto loggedInUser = (LoginResponseDto) userAttr;

            User admin = validateAdmin(loggedInUser.getUserId());

            Notification notify = new Notification(
                    req.getTitle(),
                    req.getMessage(),
                    req.getType().name(),
                    req.getPriority().name(),
                    loggedInUser.getUserId(),
                    LocalDateTime.now()
            );

            notify = notificationRepo.save(notify);
            log.info("Notification record saved: ID {}", notify.getId());

            List<User> targetUsers = resolveTargetUsers(req);
            saveTargets(req, notify.getId(), targetUsers);

            log.info("Dispatching to {} users...", targetUsers.size());

            for (User user : targetUsers) {
                NotificationStatus status_1 = isApprovalNotification(req.getType()) ? NotificationStatus.PENDING : null;

                UserNotification userNotification = UserNotification.builder()
                        .userId(user.getId())
                        .notificationId(notify.getId())
                        .title(notify.getTitle())
                        .message((notify.getMessage()))
                        .type(notify.getType())
                        .priority(notify.getPriority())
                        .isRead(false)
                        .status(status_1 != null ? status_1.name() : null)
                        .receiveAt(LocalDateTime.now())
                        .build();

                userNotification = userNotificationRepo.save(userNotification);

                NotificationResponseDto response = NotificationResponseDto.builder()
                        .userNotificationId(userNotification.getId())
                        .notificationId(notify.getId())
                        .userId(user.getId())
                        .title(userNotification.getTitle())
                        .message(userNotification.getMessage())
                        .type(userNotification.getType())
                        .priority(userNotification.getPriority())
                        .isRead(userNotification.getIsRead())
                        .status(userNotification.getStatus())
                        .receivedAt(userNotification.getReceiveAt())
                        .senderName(admin.getName())
                        .senderDepartment(admin.getDepartment() != null ? admin.getDepartment().getDepartment_name() : "System")
                        .build();

                messagingTemplate.convertAndSend(
                        "/topic/notification/" + user.getId(),
                        response
                );
            }
            log.info("Broadcast completed successfully for notification ID: {}", notify.getId());
        } catch (SessionExpiredException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Fatal error in send(): {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        } finally {
            log.debug("Exiting send() for: {}", req.getTitle());
        }
    }

    private boolean isApprovalNotification(NotificationType type) {
        if(type ==NotificationType.APPROVED_REQUEST || type==NotificationType.ACTION_REQUIRED){
            return true;
        }
        else return false;
    }

    private void saveTargets(NotificationRequestDto req, long notificationId, List<User> targetUsers) {
        switch (req.getTargetType()) {
            case ALL:
                notificationTargetRepo.save(
                        NotificationTarget.builder()
                                .notificationId(notificationId)
                                .targetType(TargetType.ALL.name())
                                .build()
                );
                break;

            case USER:
                for (User user : targetUsers) {
                    notificationTargetRepo.save(
                            NotificationTarget.builder()
                                    .notificationId(notificationId)
                                    .targetType(TargetType.USER.name())
                                    .userId(user.getId())
                                    .build()
                    );
                }
                break;

            case DEPARTMENT:
                notificationTargetRepo.save(
                        NotificationTarget.builder()
                                .notificationId(notificationId)
                                .targetType(TargetType.DEPARTMENT.name())
                                .departmentId(req.getDepartmentId())
                                .build()
                );
                break;
        }

    }


    private User validateAdmin(Long adminId) {
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + adminId));

//        this is the wrong way to check
//        -------------------------------------
//        if(admin.getRole().getRole_name().equals("ADMIN") || admin.getRoleId()!=1L)
//        {
//            throw new RuntimeException("Access denied: Not admin");
//        }
//        -------------------------------------------------
        if (!"ADMIN".equals(admin.getRole().getRole_name())
                && !admin.getRoleId().equals(1L)) {

            throw new RuntimeException("Access Denied: Admin privileges required");
        }
        log.info("Admin validated: {}", admin.getEmail());
        return admin;
    }

    @Override
    public List<com.peoplestrong.NotificationSystem.DTO.Response.SentNotificationResponseDto> getSentHistory(HttpSession session) {
        try {
            Object userAttr = session.getAttribute("LOGIN_USER");
            if (userAttr == null) throw new SessionExpiredException("Session Expired");
            LoginResponseDto loggedInUser = (LoginResponseDto) userAttr;

            List<Notification> sentNotifications = notificationRepo.findByCreatedByOrderByCreatedAtDesc(loggedInUser.getUserId());
            List<com.peoplestrong.NotificationSystem.DTO.Response.SentNotificationResponseDto> history = new java.util.ArrayList<>();

            for (Notification n : sentNotifications) {
                List<NotificationTarget> targets = notificationTargetRepo.findByNotificationId(n.getId());

                List<String> targetNames = new java.util.ArrayList<>();
                List<Long> targetIds = new java.util.ArrayList<>();
                String targetType = "ALL";

                if (!targets.isEmpty()) {
                    targetType = targets.get(0).getTargetType();
                    for (NotificationTarget t : targets) {
                        if ("USER".equals(targetType) && t.getUser() != null) {
                            targetNames.add(t.getUser().getName());
                            targetIds.add(t.getUserId());
                        } else if ("DEPARTMENT".equals(targetType) && t.getDepartment() != null) {
                            targetNames.add(t.getDepartment().getDepartment_name());
                            targetIds.add(t.getDepartmentId());
                        }
                    }
                }

                history.add(com.peoplestrong.NotificationSystem.DTO.Response.SentNotificationResponseDto.builder()
                        .notificationId(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .type(n.getType())
                        .priority(n.getPriority())
                        .createdAt(n.getCreatedAt())
                        .targetType(targetType)
                        .targetNames(targetNames)
                        .targetIds(targetIds)
                        .build());
            }
            return history;
        } catch (SessionExpiredException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching sent history: {}", e.getMessage(), e);
            throw new RuntimeException("Could not load history", e);
        } finally {
            log.debug("History retrieval completed");
        }
    }

    List<User> resolveTargetUsers(NotificationRequestDto dto) {
        switch (dto.getTargetType()) {
            case ALL:
                return userRepo.findAll();
            case USER:
                return userRepo.findAllById(dto.getUserIds());
            case DEPARTMENT:
                return userRepo.findByDepartmentId(dto.getDepartmentId());
            default:
                throw new IllegalArgumentException("Unsupported target type: " + dto.getTargetType());
        }
    }
}

