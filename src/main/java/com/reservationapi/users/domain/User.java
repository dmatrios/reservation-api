package com.reservationapi.users.domain;

import com.reservationapi.reservations.domain.Reservation;
import com.reservationapi.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String email;

    /**
     * Guardaremos el hash (BCrypt) en FASE 7.
     * Por ahora es solo el campo persistido.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 15)
    private String dni;

    @Column(nullable = false, length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();


    public static User createUserRegistration(
            String email,
            String passwordHash,
            String fullName,
            String dni,
            String phone
    ) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setFullName(fullName);
        u.setDni(dni);
        u.setPhone(phone);
        u.setRole(UserRole.USER);
        u.setStatus(UserStatus.INACTIVE);
        return u;
    }
    public static User createBootstrapAdmin(
            String email,
            String passwordHash,
            String fullName,
            String dni,
            String phone
    ) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setFullName(fullName);
        u.setDni(dni);
        u.setPhone(phone);
        u.setRole(UserRole.ADMIN);
        u.setStatus(UserStatus.ACTIVE);
        return u;
    }
}