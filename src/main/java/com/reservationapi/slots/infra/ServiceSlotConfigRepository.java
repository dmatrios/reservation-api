package com.reservationapi.slots.infra;

import com.reservationapi.slots.domain.ServiceSlotConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceSlotConfigRepository extends JpaRepository<ServiceSlotConfig, Long> {}