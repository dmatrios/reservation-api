package com.reservationapi.rooms.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomCreateRequest {

    @NotBlank
    @Size(max = 40)
    private String code;

    @NotNull
    @Min(1)
    private Integer capacity;
}