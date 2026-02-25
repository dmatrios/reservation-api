package com.reservationapi.rooms.web.dto;

import com.reservationapi.rooms.domain.RoomStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {
    private Long id;
    private String code;
    private Integer capacity;
    private RoomStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}