package com.reservationapi.security.support;

import com.reservationapi.security.principal.UserPrincipal;
import com.reservationapi.users.domain.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getUserId() {
        return requirePrincipal().getId();
    }

    public String getEmail() {
        return requirePrincipal().getEmail();
    }

    public UserRole getRole() {
        return requirePrincipal().getRole();
    }

    private UserPrincipal requirePrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal up) {
            return up;
        }

        throw new IllegalStateException("Invalid principal type: " + principal);
    }
}