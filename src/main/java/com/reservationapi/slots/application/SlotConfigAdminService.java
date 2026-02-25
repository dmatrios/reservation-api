package com.reservationapi.slots.application;

import com.reservationapi.reservations.domain.ServiceType;
import com.reservationapi.shared.exceptions.ConflictException;
import com.reservationapi.shared.exceptions.ErrorCode;
import com.reservationapi.shared.exceptions.NotFoundException;
import com.reservationapi.slots.application.mapper.SlotConfigMapper;
import com.reservationapi.slots.domain.ServiceSlotConfig;
import com.reservationapi.slots.infra.ServiceSlotConfigRepository;
import com.reservationapi.slots.web.dto.SlotConfigCreateRequest;
import com.reservationapi.slots.web.dto.SlotConfigResponse;
import com.reservationapi.slots.web.dto.SlotConfigUpdateRequest;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlotConfigAdminService {

    private final ServiceSlotConfigRepository slotConfigRepository;

    @Transactional
    public SlotConfigResponse create(SlotConfigCreateRequest request) {
        validateAllowedType(request.getServiceType());
        validateTimeRange(request.getStartTime(), request.getEndTime());

        ServiceSlotConfig sc = ServiceSlotConfig.create(
                request.getServiceType(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCapacity(),
                request.getActive()
        );

        slotConfigRepository.save(sc);
        return SlotConfigMapper.toResponse(sc);
    }

    @Transactional(readOnly = true)
    public List<SlotConfigResponse> list(ServiceType serviceType) {
        // Si quieres filtrar por serviceType sin tocar repo, filtramos en memoria.
        // (Luego lo optimizamos con query si hace falta.)
        return slotConfigRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .filter(sc -> serviceType == null || sc.getServiceType() == serviceType)
                .map(SlotConfigMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SlotConfigResponse getById(Long id) {
        ServiceSlotConfig sc = slotConfigRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SlotConfig not found."));
        return SlotConfigMapper.toResponse(sc);
    }

    @Transactional
    public SlotConfigResponse update(Long id, SlotConfigUpdateRequest request) {
        ServiceSlotConfig sc = slotConfigRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SlotConfig not found."));

        if (request.getServiceType() != null) {
            validateAllowedType(request.getServiceType());
        }

        LocalTime start = request.getStartTime() != null ? request.getStartTime() : sc.getStartTime();
        LocalTime end = request.getEndTime() != null ? request.getEndTime() : sc.getEndTime();
        validateTimeRange(start, end);

        sc.applyUpdate(
                request.getServiceType(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCapacity(),
                request.getActive()
        );

        slotConfigRepository.save(sc);
        return SlotConfigMapper.toResponse(sc);
    }

    @Transactional
    public SlotConfigResponse updateActive(Long id, boolean active) {
        ServiceSlotConfig sc = slotConfigRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SlotConfig not found."));

        if (sc.getActive() != active) {
            sc.setActive(active);
            slotConfigRepository.save(sc);
        }

        return SlotConfigMapper.toResponse(sc);
    }

    private void validateAllowedType(ServiceType serviceType) {
        if (serviceType == ServiceType.HOTEL) {
            // Reusamos BAD_REQUEST si no quieres agregar nuevos ErrorCode
            throw new ConflictException(ErrorCode.BAD_REQUEST, "SlotConfig cannot be of type HOTEL.");
        }
    }

    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new ConflictException(ErrorCode.BAD_REQUEST, "startTime and endTime are required.");
        }
        if (!start.isBefore(end)) {
            throw new ConflictException(ErrorCode.BAD_REQUEST, "startTime must be before endTime.");
        }
    }
}