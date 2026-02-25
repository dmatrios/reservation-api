package com.reservationapi.users.application.mapper;

import com.reservationapi.users.domain.User;
import com.reservationapi.users.web.dto.UserAdminListItemResponse;

public final class UserMapper {

    private UserMapper() {}

    public static UserAdminListItemResponse toAdminListItem(User u) {
        return UserAdminListItemResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .dni(u.getDni())
                .phone(u.getPhone())
                .role(u.getRole())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}