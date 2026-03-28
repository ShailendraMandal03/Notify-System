package com.peoplestrong.NotificationSystem.Repository;

import com.peoplestrong.NotificationSystem.Entity.NotificationTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget,Long> {
    java.util.List<NotificationTarget> findByNotificationId(Long notificationId);
}
