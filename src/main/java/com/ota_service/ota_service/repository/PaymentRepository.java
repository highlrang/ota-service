package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentNo(String paymentNo);
}
