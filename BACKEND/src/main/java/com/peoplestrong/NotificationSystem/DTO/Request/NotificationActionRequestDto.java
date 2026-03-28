package com.peoplestrong.NotificationSystem.DTO.Request;

import com.peoplestrong.NotificationSystem.enums.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationActionRequestDto {
    @NotNull
    private ActionType actionType;
}
