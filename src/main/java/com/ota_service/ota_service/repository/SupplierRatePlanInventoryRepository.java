package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.SupplierRatePlanInventory;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRatePlanInventoryRepository extends JpaRepository<SupplierRatePlanInventory, Long> {

    Optional<SupplierRatePlanInventory> findByRoomRateIdAndInventoryDate(Long roomRateId, LocalDate inventoryDate);
}
