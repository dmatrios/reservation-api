package com.reservationapi.users.web.dto;

import com.reservationapi.users.domain.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateRequest {

    @NotNull(message = "status is required")
    private UserStatus status;
}