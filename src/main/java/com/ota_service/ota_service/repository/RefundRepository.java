package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Refund;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByRefundNo(String refundNo);
}
