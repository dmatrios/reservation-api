package com.reservationapi.shared.web;

import com.reservationapi.users.domain.UserRole;
import org.springframework.stereotype.Component;

@Component
public class RequesterResolver {

    public RequesterContext fromHeaders(String userIdHeader, String roleHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new IllegalArgumentException("Missing X-User-Id header");
        }
        if (roleHeader == null || roleHeader.isBlank()) {
            throw new IllegalArgumentException("Missing X-User-Role header");
        }

        Long userId = Long.parseLong(userIdHeader.trim());
        UserRole role = UserRole.valueOf(roleHeader.trim().toUpperCase());

        return new RequesterContext(userId, role);
    }
}