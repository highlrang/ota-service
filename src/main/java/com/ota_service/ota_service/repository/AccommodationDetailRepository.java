package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.AccommodationDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationDetailRepository extends JpaRepository<AccommodationDetail, Long> {

    Optional<AccommodationDetail> findByAccommodationId(Long accommodationId);
}
