package com.reservationapi.reservations.application.mapper;

import com.reservationapi.reservations.domain.Reservation;
import com.reservationapi.reservations.web.dto.ReservationListItemResponse;
import com.reservationapi.reservations.web.dto.ReservationResponse;

public final class ReservationMapper {

    private ReservationMapper() {}

    public static ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .serviceType(r.getServiceType())
                .status(r.getStatus())
                .dni(r.getDniSnapshot())
                .phone(r.getPhoneSnapshot())
                .email(r.getEmailSnapshot())
                .fullName(r.getFullNameSnapshot())
                .partySize(r.getPartySize())
                .note(r.getNote())
                .roomId(r.getRoom() != null ? r.getRoom().getId() : null)
                .checkInDate(r.getCheckInDate())
                .checkOutDate(r.getCheckOutDate())
                .slotConfigId(r.getSlotConfig() != null ? r.getSlotConfig().getId() : null)
                .reservationDate(r.getReservationDate())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    public static ReservationListItemResponse toListItem(Reservation r) {
        return ReservationListItemResponse.builder()
                .id(r.getId())
                .serviceType(r.getServiceType())
                .status(r.getStatus())
                .partySize(r.getPartySize())
                .checkInDate(r.getCheckInDate())
                .checkOutDate(r.getCheckOutDate())
                .reservationDate(r.getReservationDate())
                .createdAt(r.getCreatedAt())
                .build();
    }
}