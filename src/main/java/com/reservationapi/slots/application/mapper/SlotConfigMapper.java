package com.reservationapi.slots.application.mapper;

import com.reservationapi.slots.domain.ServiceSlotConfig;
import com.reservationapi.slots.web.dto.SlotConfigResponse;

public final class SlotConfigMapper {

    private SlotConfigMapper() {}

    public static SlotConfigResponse toResponse(ServiceSlotConfig sc) {
        return SlotConfigResponse.builder()
                .id(sc.getId())
                .serviceType(sc.getServiceType())
                .startTime(sc.getStartTime())
                .endTime(sc.getEndTime())
                .capacity(sc.getCapacity())
                .active(sc.getActive())
                .createdAt(sc.getCreatedAt())
                .updatedAt(sc.getUpdatedAt())
                .build();
    }
}