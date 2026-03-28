package com.peoplestrong.NotificationSystem.controller;

import com.peoplestrong.NotificationSystem.DTO.Response.DepartmentMetadataDto;
import com.peoplestrong.NotificationSystem.DTO.Response.UserMetadataDto;
import com.peoplestrong.NotificationSystem.Service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Metadata", description = "Endpoints for fetching auxiliary data like user search and departments")
public class MetadataController {

    private final MetadataService metadataService;

    @GetMapping("/users/search")
    @Operation(summary = "Search Users by Email", description = "Returns a list of users whose email contains the given string (debounced search)")
    public ResponseEntity<List<UserMetadataDto>> searchUsers(@RequestParam String email) {
        log.info("Received request to search users by email: {}", email);
        return ResponseEntity.ok(metadataService.searchUsersByEmail(email));
    }

    @GetMapping("/departments")
    @Operation(summary = "Get All Departments", description = "Returns a list of all available departments for selection")
    public ResponseEntity<List<DepartmentMetadataDto>> getAllDepartments() {
        log.info("Received request to fetch all departments");
        return ResponseEntity.ok(metadataService.getAllDepartments());
    }
}
