package com.peoplestrong.NotificationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.PrivateKey;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    private String password;

    @Column(name="role_id")
    private Long roleId;

    @Column(name="department_id")
    private Long departmentId;

    @Builder.Default
    @Column(name="isactive")
    private Boolean isActive=true;

    @Builder.Default
    @Column(name="isonline")
    private Boolean isOnline=false;

    @Column(name="createdat")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id",insertable = false,updatable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="department_id",insertable = false,updatable = false)
    private Department department;
}
