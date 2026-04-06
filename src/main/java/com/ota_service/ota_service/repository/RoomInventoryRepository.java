package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.RoomInventory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("""
            select ri
            from RoomInventory ri
            where ri.roomId = :roomId
              and ri.inventoryDate between :startDate and :endDate
              and ri.active = true
            order by ri.inventoryDate asc
            """)
    List<RoomInventory> findAllByRoomIdAndInventoryDateBetweenAndActiveTrueForUpdate(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<RoomInventory> findAllByRoomIdInAndActiveTrueOrderByRoomIdAscInventoryDateAsc(List<Long> roomIds);
}
