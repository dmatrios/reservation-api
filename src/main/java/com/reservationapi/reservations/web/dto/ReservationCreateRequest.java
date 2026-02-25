package com.reservationapi.reservations.web.dto;

import com.reservationapi.reservations.domain.ServiceType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservationCreateRequest {

    @NotNull
    private ServiceType serviceType;

    @NotBlank
    @Size(max = 15)
    private String dni;

    @NotBlank
    @Size(max = 30)
    private String phone;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotNull
    @Min(1)
    private Integer partySize;

    @Size(max = 500)
    private String note;

    // HOTEL (required if serviceType=HOTEL, validated in service)
    @Positive
    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    // RESTAURANT/POOL (required if serviceType=RESTAURANT/POOL, validated in service)
    @Positive
    private Long slotConfigId;

    private LocalDate reservationDate;
}