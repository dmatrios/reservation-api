package com.reservationapi.reservations.application;

import com.reservationapi.reservations.application.mapper.ReservationMapper;
import com.reservationapi.reservations.domain.Reservation;
import com.reservationapi.reservations.domain.ReservationStatus;
import com.reservationapi.reservations.domain.ServiceType;
import com.reservationapi.reservations.infra.ReservationRepository;
import com.reservationapi.rooms.domain.Room;
import com.reservationapi.rooms.domain.RoomStatus;
import com.reservationapi.rooms.infra.RoomRepository;
import com.reservationapi.shared.exceptions.BadRequestException;
import com.reservationapi.shared.exceptions.ConflictException;
import com.reservationapi.shared.exceptions.ErrorCode;
import com.reservationapi.shared.exceptions.ForbiddenException;
import com.reservationapi.shared.exceptions.NotFoundException;
import com.reservationapi.slots.domain.ServiceSlotConfig;
import com.reservationapi.slots.infra.ServiceSlotConfigRepository;
import com.reservationapi.users.domain.User;
import com.reservationapi.users.domain.UserRole;
import com.reservationapi.users.infra.UserRepository;
import com.reservationapi.reservations.web.dto.ReservationCreateRequest;
import com.reservationapi.reservations.web.dto.ReservationListItemResponse;
import com.reservationapi.reservations.web.dto.ReservationResponse;
import com.reservationapi.reservations.web.dto.ReservationUpdateRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final List<ReservationStatus> ACTIVE_STATUSES =
            List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ServiceSlotConfigRepository slotConfigRepository;

    @Transactional
    public ReservationResponse create(Long requesterUserId, UserRole requesterRole, ReservationCreateRequest req) {
        // En esta fase asumimos que create lo hará USER, pero igual lo validamos.
        if (requesterRole != UserRole.USER && requesterRole != UserRole.ADMIN) {
            throw new ForbiddenException("Requester role not allowed.");
        }

        User user = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Reservation r = Reservation.createPending(user);

        // snapshot
        r.setDniSnapshot(req.getDni());
        r.setPhoneSnapshot(req.getPhone());
        r.setEmailSnapshot(req.getEmail());
        r.setFullNameSnapshot(req.getFullName());

        r.setPartySize(req.getPartySize());
        r.setNote(req.getNote());
        r.setServiceType(req.getServiceType());
        r.setStatus(ReservationStatus.PENDING);

        // Validación por tipo + set de campos opcionales
        if (req.getServiceType() == ServiceType.HOTEL) {
            applyHotelFieldsForCreate(r, req);
            validateHotelAvailability(r.getRoom().getId(), r.getCheckInDate(), r.getCheckOutDate(), null);
            validateHotelCapacity(r.getRoom(), r.getPartySize());
        } else {
            applySlotFieldsForCreate(r, req);
            validateSlotTypeMatch(r.getServiceType(), r.getSlotConfig());
            validateSlotCapacity(r.getSlotConfig().getId(), r.getReservationDate(), r.getPartySize(), null);
        }

        Reservation saved = reservationRepository.save(r);
        return ReservationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservationListItemResponse> getMine(Long requesterUserId) {
        return reservationRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(requesterUserId)
                .stream()
                .map(ReservationMapper::toListItem)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponse getById(Long requesterUserId, UserRole requesterRole, Long reservationId) {
        Reservation r = getReservationOrThrow(reservationId);
        ensureNotSoftDeleted(r);
        ensureAccess(r, requesterUserId, requesterRole);
        return ReservationMapper.toResponse(r);
    }

    @Transactional
    public ReservationResponse update(Long requesterUserId, UserRole requesterRole, Long reservationId, ReservationUpdateRequest req) {
        Reservation r = getReservationOrThrow(reservationId);
        ensureNotSoftDeleted(r);
        ensureAccess(r, requesterUserId, requesterRole);

        // USER solo si PENDING
        if (requesterRole == UserRole.USER && r.getStatus() != ReservationStatus.PENDING) {
            throw new ConflictException(ErrorCode.INVALID_STATUS_TRANSITION, "USER can only update a PENDING reservation.");
        }

        // updates comunes
        if (req.getPartySize() != null) r.setPartySize(req.getPartySize());
        if (req.getNote() != null) r.setNote(req.getNote());

        if (r.getServiceType() == ServiceType.HOTEL) {
            // Si viene cualquier campo hotel, re-aplicamos conjunto y revalidamos
            boolean touchesHotelFields =
                    req.getRoomId() != null || req.getCheckInDate() != null || req.getCheckOutDate() != null;

            if (touchesHotelFields) {
                applyHotelFieldsForUpdate(r, req);
            }

            // Siempre que sea hotel, revalidamos disponibilidad/capacidad cuando pudo cambiar partySize o fechas
            validateHotelAvailability(r.getRoom().getId(), r.getCheckInDate(), r.getCheckOutDate(), r.getId());
            validateHotelCapacity(r.getRoom(), r.getPartySize());

        } else {
            boolean touchesSlotFields =
                    req.getSlotConfigId() != null || req.getReservationDate() != null;

            if (touchesSlotFields) {
                applySlotFieldsForUpdate(r, req);
            }

            validateSlotTypeMatch(r.getServiceType(), r.getSlotConfig());
            validateSlotCapacity(r.getSlotConfig().getId(), r.getReservationDate(), r.getPartySize(), r.getId());
        }

        return ReservationMapper.toResponse(r);
    }

    @Transactional
    public ReservationResponse cancel(Long requesterUserId, UserRole requesterRole, Long reservationId) {
        Reservation r = getReservationOrThrow(reservationId);
        ensureNotSoftDeleted(r);
        ensureAccess(r, requesterUserId, requesterRole);

        if (r.getStatus() == ReservationStatus.CANCELLED) {
            return ReservationMapper.toResponse(r);
        }

        if (r.getStatus() == ReservationStatus.PENDING) {
            // USER dueño o ADMIN
            r.setStatus(ReservationStatus.CANCELLED);
            return ReservationMapper.toResponse(r);
        }

        // CONFIRMED -> CANCELLED solo ADMIN
        if (r.getStatus() == ReservationStatus.CONFIRMED && requesterRole == UserRole.ADMIN) {
            r.setStatus(ReservationStatus.CANCELLED);
            return ReservationMapper.toResponse(r);
        }

        throw new ConflictException(ErrorCode.INVALID_STATUS_TRANSITION, "Cannot cancel reservation in current status.");
    }

    @Transactional
    public ReservationResponse confirm(Long requesterUserId, UserRole requesterRole, Long reservationId) {
        if (requesterRole != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can confirm reservations.");
        }

        Reservation r = getReservationOrThrow(reservationId);
        ensureNotSoftDeleted(r);

        if (r.getStatus() != ReservationStatus.PENDING) {
            throw new ConflictException(ErrorCode.INVALID_STATUS_TRANSITION, "Only PENDING reservations can be confirmed.");
        }

        // Revalidación antes de confirmar (pro)
        if (r.getServiceType() == ServiceType.HOTEL) {
            if (r.getRoom() == null || r.getCheckInDate() == null || r.getCheckOutDate() == null) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST, "HOTEL reservation missing room/dates.");
            }
            validateHotelAvailability(r.getRoom().getId(), r.getCheckInDate(), r.getCheckOutDate(), r.getId());
            validateHotelCapacity(r.getRoom(), r.getPartySize());
        } else {
            if (r.getSlotConfig() == null || r.getReservationDate() == null) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST, "SLOT reservation missing slot/date.");
            }
            validateSlotTypeMatch(r.getServiceType(), r.getSlotConfig());
            validateSlotCapacity(r.getSlotConfig().getId(), r.getReservationDate(), r.getPartySize(), r.getId());
        }

        r.setStatus(ReservationStatus.CONFIRMED);
        return ReservationMapper.toResponse(r);
    }

    @Transactional
    public ReservationResponse softDelete(Long requesterUserId, UserRole requesterRole, Long reservationId) {
        if (requesterRole != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can delete reservations.");
        }

        Reservation r = getReservationOrThrow(reservationId);
        if (r.getDeletedAt() != null) {
            return ReservationMapper.toResponse(r);
        }

        r.setDeletedAt(Instant.now());
        return ReservationMapper.toResponse(r);
    }

    // ---------- Helpers ----------

    private Reservation getReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found."));
    }

    private void ensureNotSoftDeleted(Reservation r) {
        if (r.getDeletedAt() != null) {
            throw new ConflictException(ErrorCode.RESERVATION_SOFT_DELETED, "Reservation is soft-deleted.");
        }
    }

    private void ensureAccess(Reservation r, Long requesterUserId, UserRole requesterRole) {
        if (requesterRole == UserRole.ADMIN) return;
        if (requesterRole == UserRole.USER && r.getUser().getId().equals(requesterUserId)) return;
        throw new ForbiddenException("You do not have access to this reservation.");
    }

    private void applyHotelFieldsForCreate(Reservation r, ReservationCreateRequest req) {
        if (req.getRoomId() == null || req.getCheckInDate() == null || req.getCheckOutDate() == null) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "HOTEL requires roomId, checkInDate and checkOutDate.");
        }
        if (!req.getCheckInDate().isBefore(req.getCheckOutDate())) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "checkInDate must be before checkOutDate.");
        }
        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room not found."));
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new ConflictException(ErrorCode.CONFLICT, "Room is not ACTIVE.");
        }
        r.setRoom(room);
        r.setCheckInDate(req.getCheckInDate());
        r.setCheckOutDate(req.getCheckOutDate());

        // Limpieza por seguridad
        r.setSlotConfig(null);
        r.setReservationDate(null);
    }

    private void applyHotelFieldsForUpdate(Reservation r, ReservationUpdateRequest req) {
        Long roomId = req.getRoomId() != null ? req.getRoomId() : (r.getRoom() != null ? r.getRoom().getId() : null);
        LocalDate checkIn = req.getCheckInDate() != null ? req.getCheckInDate() : r.getCheckInDate();
        LocalDate checkOut = req.getCheckOutDate() != null ? req.getCheckOutDate() : r.getCheckOutDate();

        if (roomId == null || checkIn == null || checkOut == null) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "HOTEL requires roomId, checkInDate and checkOutDate.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "checkInDate must be before checkOutDate.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found."));
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new ConflictException(ErrorCode.CONFLICT, "Room is not ACTIVE.");
        }

        r.setRoom(room);
        r.setCheckInDate(checkIn);
        r.setCheckOutDate(checkOut);

        r.setSlotConfig(null);
        r.setReservationDate(null);
    }

    private void applySlotFieldsForCreate(Reservation r, ReservationCreateRequest req) {
        if (req.getSlotConfigId() == null || req.getReservationDate() == null) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "RESTAURANT/POOL requires slotConfigId and reservationDate.");
        }
        ServiceSlotConfig slot = slotConfigRepository.findById(req.getSlotConfigId())
                .orElseThrow(() -> new NotFoundException("Slot config not found."));
        if (!Boolean.TRUE.equals(slot.getActive())) {
            throw new ConflictException(ErrorCode.CONFLICT, "Slot config is not active.");
        }

        r.setSlotConfig(slot);
        r.setReservationDate(req.getReservationDate());

        // Limpieza por seguridad
        r.setRoom(null);
        r.setCheckInDate(null);
        r.setCheckOutDate(null);
    }

    private void applySlotFieldsForUpdate(Reservation r, ReservationUpdateRequest req) {
        Long slotId = req.getSlotConfigId() != null ? req.getSlotConfigId() : (r.getSlotConfig() != null ? r.getSlotConfig().getId() : null);
        LocalDate date = req.getReservationDate() != null ? req.getReservationDate() : r.getReservationDate();

        if (slotId == null || date == null) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "RESTAURANT/POOL requires slotConfigId and reservationDate.");
        }

        ServiceSlotConfig slot = slotConfigRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Slot config not found."));
        if (!Boolean.TRUE.equals(slot.getActive())) {
            throw new ConflictException(ErrorCode.CONFLICT, "Slot config is not active.");
        }

        r.setSlotConfig(slot);
        r.setReservationDate(date);

        r.setRoom(null);
        r.setCheckInDate(null);
        r.setCheckOutDate(null);
    }

    private void validateHotelAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeReservationId) {
        boolean overlap = (excludeReservationId == null)
                ? reservationRepository.existsActiveHotelOverlap(roomId, checkIn, checkOut, ACTIVE_STATUSES)
                : reservationRepository.existsActiveHotelOverlapExcluding(roomId, checkIn, checkOut, excludeReservationId, ACTIVE_STATUSES);

        if (overlap) {
            throw new ConflictException(ErrorCode.ROOM_OVERLAP, "Room is not available for the selected dates.");
        }
    }

    private void validateHotelCapacity(Room room, Integer partySize) {
        if (partySize == null || partySize < 1) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "partySize must be >= 1.");
        }
        if (room.getCapacity() != null && partySize > room.getCapacity()) {
            throw new ConflictException(ErrorCode.CONFLICT, "partySize exceeds room capacity.");
        }
    }

    private void validateSlotTypeMatch(ServiceType reservationType, ServiceSlotConfig slot) {
        if (slot.getServiceType() != reservationType) {
            throw new ConflictException(ErrorCode.SLOT_SERVICE_TYPE_MISMATCH, "slotConfig.serviceType does not match reservation serviceType.");
        }
    }

    private void validateSlotCapacity(Long slotConfigId, LocalDate date, Integer partySize, Long excludeReservationId) {
        if (partySize == null || partySize < 1) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "partySize must be >= 1.");
        }

        long occupied = (excludeReservationId == null)
                ? reservationRepository.sumPartySizeForSlotAndDate(slotConfigId, date, ACTIVE_STATUSES)
                : reservationRepository.sumPartySizeForSlotAndDateExcluding(slotConfigId, date, excludeReservationId, ACTIVE_STATUSES);

        ServiceSlotConfig slot = slotConfigRepository.findById(slotConfigId)
                .orElseThrow(() -> new NotFoundException("Slot config not found."));

        long capacity = slot.getCapacity() != null ? slot.getCapacity() : 0;
        if (occupied + partySize > capacity) {
            throw new ConflictException(ErrorCode.SLOT_CAPACITY_EXCEEDED, "Slot capacity exceeded for the selected date.");
        }
    }
    @Transactional(readOnly = true)
    public Page<ReservationListItemResponse> adminListReservations(
            ReservationStatus status,
            ServiceType serviceType,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? 20 : Math.min(size, 100);

        var pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return reservationRepository.adminList(status, serviceType, pageable)
                .map(ReservationMapper::toListItem);
    }
}