package com.peoplestrong.NotificationSystem.Service;

import com.peoplestrong.NotificationSystem.DTO.Response.DepartmentMetadataDto;
import com.peoplestrong.NotificationSystem.DTO.Response.UserMetadataDto;

import java.util.List;

public interface MetadataService {
    List<UserMetadataDto> searchUsersByEmail(String email);
    List<DepartmentMetadataDto> getAllDepartments();
}
