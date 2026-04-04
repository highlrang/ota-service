package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.RoomRate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRateRepository extends JpaRepository<RoomRate, Long> {

    List<RoomRate> findAllByRoomIdInAndActiveTrueAndRateDateBetweenOrderByRoomIdAscRateDateAsc(
            List<Long> roomIds,
            LocalDate startDate,
            LocalDate endDate
    );

    List<RoomRate> findAllByRoomIdAndActiveTrueAndRateDateBetweenOrderByRateDateAsc(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<RoomRate> findAllByRoomIdInAndActiveTrueAndRateDateGreaterThanEqualOrderByRoomIdAscRateDateAsc(
            List<Long> roomIds,
            LocalDate baseDate
    );

    Optional<RoomRate> findByRoomIdAndRateDateAndActiveTrue(Long roomId, LocalDate rateDate);
}
