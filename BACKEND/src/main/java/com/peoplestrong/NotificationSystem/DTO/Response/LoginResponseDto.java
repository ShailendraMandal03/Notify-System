package com.peoplestrong.NotificationSystem.DTO.Response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
//    private String Token;
    private Long userId;
    private String name;
    private String email;
    private String role_name;
}
