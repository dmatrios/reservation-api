package com.reservationapi.slots.web.dto;

import com.reservationapi.reservations.domain.ServiceType;
import java.time.Instant;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlotConfigResponse {

    private Long id;
    private ServiceType serviceType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private Boolean active;

    private Instant createdAt;
    private Instant updatedAt;
}