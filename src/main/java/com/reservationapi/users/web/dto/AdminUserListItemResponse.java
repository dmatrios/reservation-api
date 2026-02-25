package com.reservationapi.users.web.dto;

import com.reservationapi.users.domain.UserRole;
import com.reservationapi.users.domain.UserStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserListItemResponse {

    private Long id;
    private String email;
    private String fullName;

    private UserRole role;
    private UserStatus status;

    private Instant createdAt;
}