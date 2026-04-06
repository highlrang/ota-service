package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.AccommodationImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {

    List<AccommodationImage> findAllByAccommodationIdOrderBySortOrderAsc(Long accommodationId);
}
