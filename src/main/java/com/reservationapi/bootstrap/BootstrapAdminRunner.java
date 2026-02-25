package com.reservationapi.bootstrap;

import com.reservationapi.users.domain.User;
import com.reservationapi.users.domain.UserRole;
import com.reservationapi.users.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BootstrapAdminRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bootstrap.admin.email:}")
    private String adminEmail;

    @Value("${bootstrap.admin.password:}")
    private String adminPassword;

    @Value("${bootstrap.admin.full-name:Admin}")
    private String adminFullName;

    @Value("${bootstrap.admin.dni:00000000}")
    private String adminDni;

    @Value("${bootstrap.admin.phone:000000000}")
    private String adminPhone;

    @Override
    @Transactional
    public void run(String... args) {
        // Si ya existe algún ADMIN, no hacemos nada
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        // Si no hay credenciales, no creamos admin (evitamos admin con pass vacío)
        if (isBlank(adminEmail) || isBlank(adminPassword)) {
            return;
        }

        // Si existe usuario con ese email, no duplicamos (y no lo elevamos automáticamente)
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        String hash = passwordEncoder.encode(adminPassword);

        User admin = User.createBootstrapAdmin(
                adminEmail.trim(),
                hash,
                adminFullName,
                adminDni,
                adminPhone
        );

        userRepository.save(admin);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}