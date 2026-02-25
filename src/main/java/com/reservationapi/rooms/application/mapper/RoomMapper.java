package com.reservationapi.rooms.application.mapper;

import com.reservationapi.rooms.domain.Room;
import com.reservationapi.rooms.web.dto.RoomResponse;

public final class RoomMapper {

    private RoomMapper() {}

    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .capacity(room.getCapacity())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}