// src/main/java/com/reservationapi/reservations/web/AdminReservationController.java
package com.reservationapi.reservations.web;

import com.reservationapi.reservations.application.ReservationService;
import com.reservationapi.reservations.domain.ReservationStatus;
import com.reservationapi.reservations.domain.ServiceType;
import com.reservationapi.reservations.web.dto.ReservationListItemResponse;
import com.reservationapi.reservations.web.dto.ReservationResponse;
import com.reservationapi.security.support.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Admin list:
     * - /admin/reservations?status=PENDING
     * - /admin/reservations?status=CONFIRMED&serviceType=HOTEL
     * - /admin/reservations?status=PENDING&page=0&size=20
     *
     * serviceType es opcional (required=false) para soportar listar por status sin filtrar por tipo.
     */
    @GetMapping
    public Page<ReservationListItemResponse> list(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // si no es admin, tu service/seguridad lo bloqueará igual,
        // pero acá mantenemos el patrón ya existente: role desde CurrentUserProvider
        currentUserProvider.getUserId(); // fuerza que haya usuario autenticado

        return reservationService.adminListReservations(status, serviceType, page, size);
    }

    @PatchMapping("/{id}/confirm")
    public ReservationResponse confirm(@PathVariable Long id) {
        Long adminId = currentUserProvider.getUserId();
        var role = currentUserProvider.getRole();
        return reservationService.confirm(adminId, role, id);
    }

    @PatchMapping("/{id}/delete")
    public ReservationResponse softDelete(@PathVariable Long id) {
        Long adminId = currentUserProvider.getUserId();
        var role = currentUserProvider.getRole();
        return reservationService.softDelete(adminId, role, id);
    }
}