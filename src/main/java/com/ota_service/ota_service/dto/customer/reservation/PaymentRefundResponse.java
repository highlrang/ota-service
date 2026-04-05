package com.ota_service.ota_service.dto.customer.reservation;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PaymentRefundResponse(
        boolean refunded,
        LocalDateTime refundedAt
) {
    public static PaymentRefundResponse refunded(LocalDateTime refundedAt) {
        return PaymentRefundResponse.builder()
                .refunded(true)
                .refundedAt(refundedAt)
                .build();
    }
}
