package com.reservationapi.slots.web.dto;

import com.reservationapi.reservations.domain.ServiceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SlotConfigCreateRequest {

    @NotNull
    private ServiceType serviceType; // Solo RESTAURANT o POOL (lo validamos en service)

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    private Boolean active;
}