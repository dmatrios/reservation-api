package com.reservationapi.shared.web;

import com.reservationapi.users.domain.UserRole;

public record RequesterContext(Long userId, UserRole role) {}