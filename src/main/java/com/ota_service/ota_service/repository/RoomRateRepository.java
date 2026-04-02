package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.RoomRate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRateRepository extends JpaRepository<RoomRate, Long> {

    List<RoomRate> findAllByRoomIdInAndActiveTrueOrderByRoomIdAscValidFromAscValidToAsc(List<Long> roomIds);
}
