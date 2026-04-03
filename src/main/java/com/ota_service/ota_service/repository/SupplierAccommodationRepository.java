package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.SupplierAccommodation;
import com.ota_service.ota_service.enums.SupplierSourceType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierAccommodationRepository extends JpaRepository<SupplierAccommodation, Long> {

    Optional<SupplierAccommodation> findBySourceAndSupplierPropertyId(SupplierSourceType source, String supplierPropertyId);
}
