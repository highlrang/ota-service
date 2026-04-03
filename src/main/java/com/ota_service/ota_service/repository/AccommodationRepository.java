package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Accommodation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Optional<Accommodation> findFirstByNameAndAddress(String name, String address);
}
