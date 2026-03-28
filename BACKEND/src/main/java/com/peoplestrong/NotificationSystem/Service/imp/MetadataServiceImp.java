package com.peoplestrong.NotificationSystem.Service.imp;

import com.peoplestrong.NotificationSystem.DTO.Response.DepartmentMetadataDto;
import com.peoplestrong.NotificationSystem.DTO.Response.UserMetadataDto;
import com.peoplestrong.NotificationSystem.Repository.DepartmentRepository;
import com.peoplestrong.NotificationSystem.Repository.UserRepository;
import com.peoplestrong.NotificationSystem.Service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataServiceImp implements MetadataService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<UserMetadataDto> searchUsersByEmail(String email) {
        log.info("Searching users by email containing: {}", email);
        return userRepository.findByEmailContainingIgnoreCase(email).stream()
                .limit(10)
                .map(user -> UserMetadataDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentMetadataDto> getAllDepartments() {
        log.info("Fetching all departments");
        return departmentRepository.findAll().stream()
                .map(dept -> DepartmentMetadataDto.builder()
                        .id(dept.getId())
                        .name(dept.getDepartment_name())
                        .build())
                .collect(Collectors.toList());
    }
}
