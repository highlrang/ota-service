package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.SupplierRoom;
import com.ota_service.ota_service.enums.SupplierSourceType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRoomRepository extends JpaRepository<SupplierRoom, Long> {

    Optional<SupplierRoom> findBySourceAndSupplierRoomtypeId(SupplierSourceType source, String supplierRoomtypeId);
}
