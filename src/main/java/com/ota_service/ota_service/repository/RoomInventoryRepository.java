package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.RoomInventory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {
    Optional<RoomInventory> findByRoomIdAndInventoryDateAndActiveTrue(Long roomId, LocalDate inventoryDate);

    List<RoomInventory> findAllByRoomIdAndInventoryDateBetweenAndActiveTrueOrderByInventoryDateAsc(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<RoomInventory> findAllByRoomIdInAndInventoryDateBetweenAndActiveTrueOrderByRoomIdAscInventoryDateAsc(
            List<Long> roomIds,
            LocalDate startDate,
            LocalDate endDate
    );

    List<RoomInventory> findAllByRoomIdInAndActiveTrueOrderByRoomIdAscInventoryDateAsc(List<Long> roomIds);
}
