package com.reservationapi.slots.web;

import com.reservationapi.reservations.domain.ServiceType;
import com.reservationapi.slots.application.SlotConfigAdminService;
import com.reservationapi.slots.web.dto.SlotConfigActiveUpdateRequest;
import com.reservationapi.slots.web.dto.SlotConfigCreateRequest;
import com.reservationapi.slots.web.dto.SlotConfigResponse;
import com.reservationapi.slots.web.dto.SlotConfigUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/slot-configs")
public class AdminSlotConfigController {

    private final SlotConfigAdminService slotConfigAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotConfigResponse create(@Valid @RequestBody SlotConfigCreateRequest request) {
        return slotConfigAdminService.create(request);
    }

    @GetMapping
    public List<SlotConfigResponse> list(@RequestParam(value = "serviceType", required = false) ServiceType serviceType) {
        return slotConfigAdminService.list(serviceType);
    }

    @GetMapping("/{id}")
    public SlotConfigResponse getById(@PathVariable Long id) {
        return slotConfigAdminService.getById(id);
    }

    @PutMapping("/{id}")
    public SlotConfigResponse update(@PathVariable Long id, @Valid @RequestBody SlotConfigUpdateRequest request) {
        return slotConfigAdminService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public SlotConfigResponse updateActive(
            @PathVariable Long id,
            @Valid @RequestBody SlotConfigActiveUpdateRequest request
    ) {
        return slotConfigAdminService.updateActive(id, request.getActive());
    }
}