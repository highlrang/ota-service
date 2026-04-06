package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Extranet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtranetRepository extends JpaRepository<Extranet, Long> {
    Optional<Extranet> findByEmail(String email);
    Optional<Extranet> findByCode(String code);
}
