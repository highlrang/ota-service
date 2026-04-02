package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.RoomInventory;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {
    Optional<RoomInventory> findByRoomIdAndInventoryDate(Long roomId, LocalDate inventoryDate);
}
