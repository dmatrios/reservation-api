package com.reservationapi.reservations.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservationUpdateRequest {

    @Min(1)
    private Integer partySize;

    @Size(max = 500)
    private String note;

    // HOTEL updates (allowed only if rules allow; validated in service)
    @Positive
    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    // RESTAURANT/POOL updates (validated in service)
    @Positive
    private Long slotConfigId;

    private LocalDate reservationDate;
}