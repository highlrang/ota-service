package com.ota_service.ota_service.dto.customer.reservation;

import java.math.BigDecimal;

public record PaymentApprovalRequest(
        String reservationNo,
        String paymentMethod,
        BigDecimal paymentAmount,
        String currency
) {
}
