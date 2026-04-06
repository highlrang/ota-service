package com.ota_service.ota_service.dto.customer.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "예약 상세 결제 정보")
@Builder
public record CustomerReservationPaymentResponse(
        @Schema(example = "PAY-260405-0001")
        String paymentNo,

        @Schema(example = "PAID")
        String paymentStatus,

        @Schema(example = "CARD")
        String paymentMethod,

        @Schema(example = "270000")
        BigDecimal paymentAmount,

        @Schema(example = "KRW")
        String currency,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime approvedAt
) {
    public static CustomerReservationPaymentResponse from(Payment payment) {
        return CustomerReservationPaymentResponse.builder()
                .paymentNo(payment.getPaymentNo())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentMethod(payment.getPaymentMethod())
                .paymentAmount(payment.getPaymentAmount())
                .currency(payment.getCurrency())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}
