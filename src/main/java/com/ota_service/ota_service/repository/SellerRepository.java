package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmail(String email);
    Optional<Seller> findByCode(String code);
}
