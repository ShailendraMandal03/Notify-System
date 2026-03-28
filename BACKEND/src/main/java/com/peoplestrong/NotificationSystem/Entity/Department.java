package com.peoplestrong.NotificationSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String department_name;
}
