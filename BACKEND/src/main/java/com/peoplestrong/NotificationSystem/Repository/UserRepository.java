package com.peoplestrong.NotificationSystem.Repository;

import com.peoplestrong.NotificationSystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User>findByEmail(String Email);
    List<User>findByEmailContainingIgnoreCase(String email);
    List<User>findByRoleId(Long roleId);
    List<User>findByDepartmentId(Long departmentId);
}
    