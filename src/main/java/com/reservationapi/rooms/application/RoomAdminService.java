package com.reservationapi.rooms.application;

import com.reservationapi.rooms.application.mapper.RoomMapper;
import com.reservationapi.rooms.domain.Room;
import com.reservationapi.rooms.domain.RoomStatus;
import com.reservationapi.rooms.infra.RoomRepository;
import com.reservationapi.rooms.web.dto.RoomCreateRequest;
import com.reservationapi.rooms.web.dto.RoomResponse;
import com.reservationapi.rooms.web.dto.RoomUpdateRequest;
import com.reservationapi.shared.exceptions.ConflictException;
import com.reservationapi.shared.exceptions.ErrorCode;
import com.reservationapi.shared.exceptions.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomAdminService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponse create(RoomCreateRequest request) {
        String code = request.getCode().trim();

        if (roomRepository.existsByCode(code)) {
            throw new ConflictException(
                    ErrorCode.ROOM_CODE_ALREADY_EXISTS,
                    "Room code already exists."
            );
        }

        Room room = Room.create(code, request.getCapacity());
        roomRepository.save(room);

        return RoomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> list() {
        return roomRepository.findAll(Sort.by(Sort.Direction.ASC, "code"))
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse getById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found."));
        return RoomMapper.toResponse(room);
    }

    @Transactional
    public RoomResponse update(Long id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found."));

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            String newCode = request.getCode().trim();
            if (!newCode.equals(room.getCode()) && roomRepository.existsByCode(newCode)) {
                throw new ConflictException(
                        ErrorCode.ROOM_CODE_ALREADY_EXISTS,
                        "Room code already exists."
                );
            }
        }

        room.applyUpdate(request.getCode(), request.getCapacity());
        roomRepository.save(room);

        return RoomMapper.toResponse(room);
    }

    @Transactional
    public RoomResponse updateStatus(Long id, RoomStatus newStatus) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found."));

        if (room.getStatus() != newStatus) {
            room.changeStatus(newStatus);
            roomRepository.save(room);
        }

        return RoomMapper.toResponse(room);
    }
}