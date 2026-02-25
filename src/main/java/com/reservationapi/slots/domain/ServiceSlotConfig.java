package com.reservationapi.slots.domain;

import com.reservationapi.reservations.domain.ServiceType;
import com.reservationapi.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "service_slot_config")
public class ServiceSlotConfig extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 20)
    private ServiceType serviceType; // SOLO RESTAURANT o POOL (lo validamos en service)

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean active;
    public static ServiceSlotConfig create(
            ServiceType serviceType,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Boolean active
    ) {
        ServiceSlotConfig sc = new ServiceSlotConfig();
        sc.setServiceType(serviceType);
        sc.setStartTime(startTime);
        sc.setEndTime(endTime);
        sc.setCapacity(capacity);
        sc.setActive(active != null ? active : true);
        return sc;
    }

    public void applyUpdate(
            ServiceType serviceType,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Boolean active
    ) {
        if (serviceType != null) this.setServiceType(serviceType);
        if (startTime != null) this.setStartTime(startTime);
        if (endTime != null) this.setEndTime(endTime);
        if (capacity != null) this.setCapacity(capacity);
        if (active != null) this.setActive(active);
    }
}