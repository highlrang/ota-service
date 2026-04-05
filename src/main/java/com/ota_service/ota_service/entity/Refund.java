package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "REFUNDS")
@Getter
@Setter
public class Refund extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "refund_no", nullable = false, unique = true, length = 100)
    private String refundNo;

    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    public static Refund create(
            Long paymentId,
            String refundNo,
            BigDecimal refundAmount,
            String refundReason,
            LocalDateTime refundedAt
    ) {
        Refund refund = new Refund();
        refund.paymentId = paymentId;
        refund.refundNo = refundNo;
        refund.refundAmount = refundAmount;
        refund.refundReason = refundReason;
        refund.refundedAt = refundedAt;
        return refund;
    }

    public void changeRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }
}
