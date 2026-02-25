package com.reservationapi.reservations.web.dto;

import com.reservationapi.reservations.domain.ReservationStatus;
import com.reservationapi.reservations.domain.ServiceType;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {

    private Long id;

    private ServiceType serviceType;
    private ReservationStatus status;

    private String dni;
    private String phone;
    private String email;
    private String fullName;

    private Integer partySize;
    private String note;

    // HOTEL
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // RESTAURANT/POOL
    private Long slotConfigId;
    private LocalDate reservationDate;

    private Instant createdAt;
    private Instant updatedAt;
}