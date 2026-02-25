package com.reservationapi.auth.web.dto;

import com.reservationapi.users.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token; // en FASE 7 (JWT). Antes puede ser null si lo dejamos stub.

    private Long userId;
    private String email;
    private UserRole role;
}