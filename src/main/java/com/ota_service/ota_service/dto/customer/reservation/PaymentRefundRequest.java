package com.ota_service.ota_service.dto.customer.reservation;

import java.math.BigDecimal;

public record PaymentRefundRequest(
        String reservationNo,
        String paymentNo,
        BigDecimal refundAmount,
        String reason
) {
}
