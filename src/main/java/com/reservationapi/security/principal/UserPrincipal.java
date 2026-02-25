// src/main/java/com/reservationapi/security/principal/UserPrincipal.java
package com.reservationapi.security.principal;

import com.reservationapi.users.domain.User;
import com.reservationapi.users.domain.UserRole;
import com.reservationapi.users.domain.UserStatus;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final UserStatus status;

    public UserPrincipal(Long id, String email, String passwordHash, UserRole role, UserStatus status) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    /**
     * PASO 7.0: no bloqueamos por status todavía.
     * En PASO 7.1 (JWT) definimos el enforcement de ACTIVE.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}