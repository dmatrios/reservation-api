package com.reservationapi.users.web;

import com.reservationapi.security.support.CurrentUserProvider;
import com.reservationapi.users.application.UserAdminService;
import com.reservationapi.users.web.dto.UserAdminListItemResponse;
import com.reservationapi.users.web.dto.UserStatusUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserAdminService userAdminService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<UserAdminListItemResponse>> listUsers() {
        return ResponseEntity.ok(userAdminService.listUsers());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserAdminListItemResponse> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        Long adminId = currentUserProvider.getUserId();
        var updated = userAdminService.updateStatus(id, request.getStatus(), adminId);
        return ResponseEntity.ok(updated);
    }
}