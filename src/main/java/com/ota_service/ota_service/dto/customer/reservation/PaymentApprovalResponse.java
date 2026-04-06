package com.ota_service.ota_service.dto.customer.reservation;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PaymentApprovalResponse(
        boolean approved,
        LocalDateTime approvedAt
) {
    public static PaymentApprovalResponse approved(LocalDateTime approvedAt) {
        return PaymentApprovalResponse.builder()
                .approved(true)
                .approvedAt(approvedAt)
                .build();
    }
}
