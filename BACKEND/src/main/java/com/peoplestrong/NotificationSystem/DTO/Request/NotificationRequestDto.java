package com.peoplestrong.NotificationSystem.DTO.Request;

import com.peoplestrong.NotificationSystem.enums.NotificationType;
import com.peoplestrong.NotificationSystem.enums.Priority;
import com.peoplestrong.NotificationSystem.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {

    @NotBlank(message="Title is required")
    private String title;

    @NotBlank(message="Message is required")
    private String message;

    @NotNull
    private NotificationType type;

    @NotNull
    private Priority priority;

    @NotNull
    private TargetType targetType;

    private List<Long> userIds;

    private Long rolesId;

    private Long departmentId;


}
