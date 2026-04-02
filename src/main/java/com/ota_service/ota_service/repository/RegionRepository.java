package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByCode(String code);
}
