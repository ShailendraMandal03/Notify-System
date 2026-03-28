package com.peoplestrong.NotificationSystem.Service.imp;

import com.peoplestrong.NotificationSystem.DTO.Request.LoginRequestDto;
import com.peoplestrong.NotificationSystem.DTO.Response.LoginResponseDto;
import com.peoplestrong.NotificationSystem.Entity.User;
import com.peoplestrong.NotificationSystem.Repository.UserRepository;
import com.peoplestrong.NotificationSystem.Service.AuthService;
import com.peoplestrong.NotificationSystem.exception.InvalidCredentialsException;
import com.peoplestrong.NotificationSystem.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {
    private final UserRepository repo;

    @Override
    public LoginResponseDto login(LoginRequestDto request, HttpSession session) {
        try {
            log.info("Login attempt for email: {}", request.getEmail());
            User user = repo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

            if (!user.getPassword().equals(request.getPassword())) {
                throw new InvalidCredentialsException("Invalid password. Please try again.");
            }

            String roleName = (user.getRole() != null) ? user.getRole().getRole_name() : "NO_ROLE_ASSIGNED";

            LoginResponseDto response = LoginResponseDto.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role_name(roleName)
                    .build();

            session.setAttribute("LOGIN_USER", response);
            log.info("User {} logged in successfully.", user.getEmail());
            return response;
        } catch (ResourceNotFoundException | InvalidCredentialsException e) {
            throw e; // Handled by GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Unexpected error during login for {}: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Login failed due to an internal error", e);
        } finally {
            log.debug("Finished login process for: {}", request.getEmail());
        }
    }
}
