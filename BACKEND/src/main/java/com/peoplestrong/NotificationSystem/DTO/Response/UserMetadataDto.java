package com.peoplestrong.NotificationSystem.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMetadataDto {
    private Long id;
    private String name;
    private String email;
}
