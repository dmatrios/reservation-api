package com.reservationapi.slots.web.dto;

import com.reservationapi.reservations.domain.ServiceType;
import jakarta.validation.constraints.Min;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SlotConfigUpdateRequest {

    private ServiceType serviceType;

    private LocalTime startTime;

    private LocalTime endTime;

    @Min(1)
    private Integer capacity;

    private Boolean active;
}