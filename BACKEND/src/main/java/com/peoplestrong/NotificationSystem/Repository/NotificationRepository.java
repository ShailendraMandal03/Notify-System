package com.peoplestrong.NotificationSystem.Repository;

import com.peoplestrong.NotificationSystem.Entity.Notification;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByCreatedByOrderByCreatedAtDesc(Long adminId);
}
