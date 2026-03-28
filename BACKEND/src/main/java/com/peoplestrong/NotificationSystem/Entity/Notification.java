package com.peoplestrong.NotificationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Entity
@Table(name="notifications")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String type;
    private String priority;

    @Column(name="created_by")
    private Long createdBy;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by",insertable = false,updatable = false)
    private User creator;

    public Notification(String title, String message, String type, String priority, Long createdBy, LocalDateTime createdAt) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
