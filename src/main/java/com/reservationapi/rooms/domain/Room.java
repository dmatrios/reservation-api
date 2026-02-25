package com.reservationapi.rooms.domain;

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
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_rooms_code", columnNames = "code")
        }
)
public class Room extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String code; // Ej: "101", "A-12"

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public static Room create(String code, Integer capacity) {
        Room r = new Room();
        r.setCode(code);
        r.setCapacity(capacity);
        r.setStatus(RoomStatus.ACTIVE);
        return r;
    }

    public void applyUpdate(String newCode, Integer newCapacity) {
        if (newCode != null && !newCode.trim().isEmpty()) {
            this.setCode(newCode.trim());
        }
        if (newCapacity != null) {
            this.setCapacity(newCapacity);
        }
    }

    public void changeStatus(RoomStatus newStatus) {
        this.setStatus(newStatus);
    }
}