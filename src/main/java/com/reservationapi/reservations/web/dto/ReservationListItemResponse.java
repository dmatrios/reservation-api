package com.reservationapi.reservations.web.dto;

import com.reservationapi.reservations.domain.ReservationStatus;
import com.reservationapi.reservations.domain.ServiceType;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationListItemResponse {

    private Long id;

    private ServiceType serviceType;
    private ReservationStatus status;

    private Integer partySize;

    // Fechas “clave” para mostrar en lista sin traer todo
    private LocalDate checkInDate;      // hotel
    private LocalDate checkOutDate;     // hotel
    private LocalDate reservationDate;  // slot

    private Instant createdAt;
}