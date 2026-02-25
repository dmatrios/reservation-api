package com.reservationapi.rooms.web.dto;

import com.reservationapi.rooms.domain.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomStatusUpdateRequest {

    @NotNull
    private RoomStatus status;
}