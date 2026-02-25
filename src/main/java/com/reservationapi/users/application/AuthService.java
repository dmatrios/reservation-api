package com.reservationapi.users.application;

import com.reservationapi.security.jwt.JwtService;
import com.reservationapi.security.principal.UserPrincipal;
import com.reservationapi.security.support.CurrentUserProvider;
import com.reservationapi.shared.exceptions.BadRequestException;
import com.reservationapi.shared.exceptions.ErrorCode;
import com.reservationapi.shared.exceptions.ForbiddenException;
import com.reservationapi.shared.exceptions.NotFoundException;
import com.reservationapi.users.domain.User;
import com.reservationapi.users.domain.UserStatus;
import com.reservationapi.users.infra.UserRepository;
import com.reservationapi.users.web.dto.AuthResponse;
import com.reservationapi.users.web.dto.ChangePasswordRequest;
import com.reservationapi.users.web.dto.LoginRequest;
import com.reservationapi.users.web.dto.RegisterRequest;
import com.reservationapi.users.web.dto.UserMeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // ✅ NUEVO
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public void register(RegisterRequest request) {

        User user = User.createUserRegistration(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getDni(),
                request.getPhone()
        );

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        if (principal.getStatus() != UserStatus.ACTIVE) {
            // Mantengo tu comportamiento
            throw new ForbiddenException("USER_NOT_ACTIVE");
        }

        String token = jwtService.generateToken(principal);

        return AuthResponse.builder()
                .token(token)
                .userId(principal.getId())
                .role(principal.getRole().name())
                .build();
    }

    // ✅ PASO 2
    @Transactional(readOnly = true)
    public UserMeResponse me() {
        Long userId = currentUserProvider.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return UserMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dni(user.getDni())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // ✅ PASO 3
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long userId = currentUserProvider.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 🔧 AJUSTA ESTO según tu entidad User:
        // si tu campo se llama "password" entonces: user.getPassword()
        // si se llama "passwordHash" entonces: user.getPasswordHash()
        String currentHash = user.getPasswordHash();

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentHash)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Current password is incorrect");
        }

        String newHash = passwordEncoder.encode(request.getNewPassword());

        // 🔧 AJUSTA ESTO según tu entidad User:
        user.setPasswordHash(newHash);
    }
}