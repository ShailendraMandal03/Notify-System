package com.peoplestrong.NotificationSystem;

import com.peoplestrong.NotificationSystem.DTO.Request.NotificationRequestDto;
import com.peoplestrong.NotificationSystem.Entity.User;
import com.peoplestrong.NotificationSystem.Repository.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValidateTest {

    private final UserRepository userRepo;

    public void send(NotificationRequestDto req, Long adminId) {
        validate(adminId);
    }

    private void validate(Long adminId) {
        User admin=userRepo.findById(adminId).orElseThrow(()->new RuntimeException("Cannot find admin data"));

        if(admin.getRole().getRole_name().equals("ADMIN") || admin.getRoleId()!=1L)
        {
            throw new RuntimeException("Access denied: Not admin");
        }
        System.out.println("Successfully validate");
    }
}
