package com.ota_service.ota_service.entity;

import com.ota_service.ota_service.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAYMENTS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "payment_no", nullable = false, unique = true, length = 100)
    private String paymentNo;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "payment_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paymentAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    public static Payment createPaid(
            Long reservationId,
            String paymentNo,
            String paymentMethod,
            BigDecimal paymentAmount,
            String currency,
            LocalDateTime approvedAt
    ) {
        Payment payment = new Payment();
        payment.reservationId = reservationId;
        payment.paymentNo = paymentNo;
        payment.paymentMethod = paymentMethod;
        payment.paymentAmount = paymentAmount;
        payment.currency = currency;
        payment.paymentStatus = PaymentStatus.PAID;
        payment.approvedAt = approvedAt;
        return payment;
    }

    public void changePaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }
}
