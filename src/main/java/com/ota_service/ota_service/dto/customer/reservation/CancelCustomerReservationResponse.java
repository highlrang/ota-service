package com.ota_service.ota_service.dto.customer.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Payment;
import com.ota_service.ota_service.entity.Refund;
import com.ota_service.ota_service.entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CancelCustomerReservationResponse(
        @Schema(example = "RSV-20260401-0001")
        String reservationNo,

        @Schema(example = "CANCELLED")
        String reservationStatus,

        @Schema(example = "PAY-20260401-0001")
        String paymentNo,

        @Schema(example = "REFUNDED")
        String paymentStatus,

        @Schema(example = "RFD-20260402-0001")
        String refundNo,

        @Schema(example = "270000")
        BigDecimal refundAmount,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime cancelledAt
) {
    public static CancelCustomerReservationResponse from(
            Reservation reservation,
            Payment payment,
            Refund refund
    ) {
        return CancelCustomerReservationResponse.builder()
                .reservationNo(reservation.getReservationNo())
                .reservationStatus(reservation.getReservationStatus().name())
                .paymentNo(payment.getPaymentNo())
                .paymentStatus(payment.getPaymentStatus().name())
                .refundNo(refund.getRefundNo())
                .refundAmount(refund.getRefundAmount())
                .cancelledAt(refund.getRefundedAt())
                .build();
    }
}
