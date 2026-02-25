package com.reservationapi.reservations.web;

import com.reservationapi.reservations.application.ReservationService;
import com.reservationapi.reservations.web.dto.ReservationCreateRequest;
import com.reservationapi.reservations.web.dto.ReservationListItemResponse;
import com.reservationapi.reservations.web.dto.ReservationResponse;
import com.reservationapi.reservations.web.dto.ReservationUpdateRequest;
import com.reservationapi.security.support.CurrentUserProvider;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationCreateRequest request) {
        Long userId = currentUserProvider.getUserId();
        var role = currentUserProvider.getRole();
        return reservationService.create(userId, role, request);
    }

    @GetMapping("/mine")
    public List<ReservationListItemResponse> mine() {
        Long userId = currentUserProvider.getUserId();
        return reservationService.getMine(userId);
    }

    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable Long id) {
        Long userId = currentUserProvider.getUserId();
        var role = currentUserProvider.getRole();
        return reservationService.getById(userId, role, id);
    }

    @PutMapping("/{id}")
    public ReservationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        Long userId = currentUserProvider.getUserId();
        var role = currentUserProvider.getRole();
        return reservationService.update(userId, role, id, request);
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id) {
        Long userId = currentUserProvider.getUserId();
        var role = currentUserProvider.getRole();
        return reservationService.cancel(userId, role, id);
    }
}