package com.reservationapi.slots.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SlotConfigActiveUpdateRequest {

    @NotNull
    private Boolean active;
}