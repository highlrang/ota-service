package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.SupplierRatePlan;
import com.ota_service.ota_service.enums.SupplierSourceType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRatePlanRepository extends JpaRepository<SupplierRatePlan, Long> {

    Optional<SupplierRatePlan> findBySourceAndSupplierRateplanId(SupplierSourceType source, String supplierRateplanId);

    Optional<SupplierRatePlan> findBySourceAndRoomRateId(SupplierSourceType source, Long roomRateId);
}
