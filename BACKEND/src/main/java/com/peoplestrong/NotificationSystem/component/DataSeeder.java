package com.peoplestrong.NotificationSystem.component;

import com.peoplestrong.NotificationSystem.Entity.Role;
import com.peoplestrong.NotificationSystem.Entity.User;
import com.peoplestrong.NotificationSystem.Repository.RoleRepository;
import com.peoplestrong.NotificationSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            Role adminRole = roleRepo.save(new Role(null, "ADMIN"));
            Role userRole = roleRepo.save(new Role(null, "USER"));

            User admin = User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password("password")
                    .role(adminRole)
                    .build();
            userRepo.save(admin);

            User testUser = User.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("password")
                    .role(userRole)
                    .build();
            userRepo.save(testUser);
            log.info("==> Created Test User: test@example.com / password");
        }
    }
}
