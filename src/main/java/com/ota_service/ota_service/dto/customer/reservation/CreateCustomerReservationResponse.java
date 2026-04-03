package com.ota_service.ota_service.dto.customer.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateCustomerReservationResponse(
        @Schema(example = "RSV-20260404-0004")
        String reservationNo,

        @Schema(example = "CONFIRMED")
        String reservationStatus,

        @Schema(example = "ACC-000001")
        String accommodationCode,

        @Schema(example = "ROOM-SEOUL-0001")
        String roomCode,

        @Schema(example = "2026-04-10")
        LocalDate checkInDate,

        @Schema(example = "2026-04-12")
        LocalDate checkOutDate,

        @Schema(example = "PAY-20260404-0004")
        String paymentNo,

        @Schema(example = "PAID")
        String paymentStatus,

        @Schema(example = "CARD")
        String paymentMethod,

        @Schema(example = "270000")
        BigDecimal paymentAmount,

        @Schema(example = "KRW")
        String currency,

        @Schema(example = "2026-04-04T15:30:00")
        LocalDateTime reservedAt
) {
}
