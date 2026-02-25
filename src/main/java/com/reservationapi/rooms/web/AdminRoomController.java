package com.reservationapi.rooms.web;

import com.reservationapi.rooms.application.RoomAdminService;
import com.reservationapi.rooms.web.dto.RoomCreateRequest;
import com.reservationapi.rooms.web.dto.RoomResponse;
import com.reservationapi.rooms.web.dto.RoomStatusUpdateRequest;
import com.reservationapi.rooms.web.dto.RoomUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/rooms")
public class AdminRoomController {

    private final RoomAdminService roomAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody RoomCreateRequest request) {
        return roomAdminService.create(request);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        return ResponseEntity.ok(roomAdminService.list());
    }

    @GetMapping("/{id}")
    public RoomResponse getById(@PathVariable Long id) {
        return roomAdminService.getById(id);
    }

    @PutMapping("/{id}")
    public RoomResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateRequest request
    ) {
        return roomAdminService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public RoomResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoomStatusUpdateRequest request
    ) {
        return roomAdminService.updateStatus(id, request.getStatus());
    }
}