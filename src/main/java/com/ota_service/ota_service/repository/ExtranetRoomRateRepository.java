package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.ExtranetRoomRate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtranetRoomRateRepository extends JpaRepository<ExtranetRoomRate, Long> {

    List<ExtranetRoomRate> findAllByRoomIdInAndActiveTrueOrderByRoomIdAscRateDateAsc(List<Long> roomIds);
}
