package com.reservationapi.users.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token;
    private Long userId;
    private String role;
}