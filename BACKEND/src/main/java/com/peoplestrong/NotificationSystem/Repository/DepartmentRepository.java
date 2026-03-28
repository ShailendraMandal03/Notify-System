package com.peoplestrong.NotificationSystem.Repository;

import com.peoplestrong.NotificationSystem.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
}
