package com.reservationapi.reservations.infra;

import com.reservationapi.reservations.domain.Reservation;
import com.reservationapi.reservations.domain.ReservationStatus;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

import com.reservationapi.reservations.domain.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    // ---- HOTEL overlap checks ----

    @Query("""
      select (count(r) > 0)
      from Reservation r
      where r.deletedAt is null
        and r.status in :activeStatuses
        and r.room.id = :roomId
        and :checkIn < r.checkOutDate
        and :checkOut > r.checkInDate
      """)
    boolean existsActiveHotelOverlap(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    @Query("""
      select (count(r) > 0)
      from Reservation r
      where r.deletedAt is null
        and r.status in :activeStatuses
        and r.room.id = :roomId
        and r.id <> :excludeId
        and :checkIn < r.checkOutDate
        and :checkOut > r.checkInDate
      """)
    boolean existsActiveHotelOverlapExcluding(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("excludeId") Long excludeId,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    // ---- SLOT capacity checks ----

    @Query("""
      select coalesce(sum(r.partySize), 0)
      from Reservation r
      where r.deletedAt is null
        and r.status in :activeStatuses
        and r.slotConfig.id = :slotConfigId
        and r.reservationDate = :date
      """)
    long sumPartySizeForSlotAndDate(
            @Param("slotConfigId") Long slotConfigId,
            @Param("date") LocalDate date,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    @Query("""
      select coalesce(sum(r.partySize), 0)
      from Reservation r
      where r.deletedAt is null
        and r.status in :activeStatuses
        and r.slotConfig.id = :slotConfigId
        and r.reservationDate = :date
        and r.id <> :excludeId
      """)
    long sumPartySizeForSlotAndDateExcluding(
            @Param("slotConfigId") Long slotConfigId,
            @Param("date") LocalDate date,
            @Param("excludeId") Long excludeId,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.deletedAt IS NULL
          AND (:status IS NULL OR r.status = :status)
          AND (:serviceType IS NULL OR r.serviceType = :serviceType)
        """)
    Page<Reservation> adminList(
            @Param("status") ReservationStatus status,
            @Param("serviceType") ServiceType serviceType,
            Pageable pageable
    );
}