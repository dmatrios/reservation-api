package com.reservationapi.reservations.domain;

import com.reservationapi.rooms.domain.Room;
import com.reservationapi.shared.domain.AuditableEntity;
import com.reservationapi.slots.domain.ServiceSlotConfig;
import com.reservationapi.users.domain.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reservations")
public class Reservation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ownership
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservations_user"))
    private User user;

    // Snapshot (datos “congelados” al reservar)
    @Column(name = "dni_snapshot", nullable = false, length = 15)
    private String dniSnapshot;

    @Column(name = "phone_snapshot", nullable = false, length = 30)
    private String phoneSnapshot;

    @Column(name = "email_snapshot", nullable = false, length = 120)
    private String emailSnapshot;

    @Column(name = "full_name_snapshot", nullable = false, length = 120)
    private String fullNameSnapshot;

    @Column(name = "party_size", nullable = false)
    private Integer partySize;

    @Column(name = "note", length = 500)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 20)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    // Soft delete
    @Column(name = "deleted_at")
    private Instant deletedAt;

    // HOTEL fields (opcionales)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_reservations_room"))
    private Room room;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    // SLOT fields (opcionales para RESTAURANT/POOL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_config_id", foreignKey = @ForeignKey(name = "fk_reservations_slot_config"))
    private ServiceSlotConfig slotConfig;

    @Column(name = "reservation_date")
    private LocalDate reservationDate;

    public static Reservation createPending(User user) {
        Reservation r = new Reservation();
        r.setUser(user);
        r.setStatus(ReservationStatus.PENDING);
        return r;
    }
}