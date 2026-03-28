package com.peoplestrong.NotificationSystem.Repository;

import com.peoplestrong.NotificationSystem.DTO.Response.NotificationResponseDto;
import com.peoplestrong.NotificationSystem.Entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification,Long> {
    List<UserNotification> findByUserIdOrderByReceiveAtDesc(Long userId);

//    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Transactional
    @Query("Update UserNotification u SET u.isRead=true,u.readAt=CURRENT_TIMESTAMP WHERE u.userId=:userId AND u.isRead=false")
    int markAllRead(@Param("userId") Long userId);


    Long countByUserIdAndIsReadFalse(Long userId);
}
