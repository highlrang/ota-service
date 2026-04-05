package com.ota_service.ota_service.dto.customer.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.Payment;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.entity.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CreateCustomerReservationResponse(
        @Schema(example = "RSV-20260404-0004")
        String reservationNo,

        @Schema(example = "CONFIRMED")
        String reservationStatus,

        @Schema(example = "ACC-000001")
        String accommodationCode,

        @Schema(example = "ROOM-SEOUL-0001")
        String roomCode,

        @Schema(example = "2026-04-10 15:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkInAt,

        @Schema(example = "2026-04-12 11:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkOutAt,

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
    public static CreateCustomerReservationResponse from(
            Reservation reservation,
            Accommodation accommodation,
            Room room,
            Payment payment
    ) {
        return CreateCustomerReservationResponse.builder()
                .reservationNo(reservation.getReservationNo())
                .reservationStatus(reservation.getReservationStatus().name())
                .accommodationCode(accommodation.getCode())
                .roomCode(room.getRoomCode())
                .checkInAt(reservation.getCheckInAt())
                .checkOutAt(reservation.getCheckOutAt())
                .paymentNo(payment.getPaymentNo())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentMethod(payment.getPaymentMethod())
                .paymentAmount(payment.getPaymentAmount())
                .currency(payment.getCurrency())
                .reservedAt(reservation.getConfirmedAt())
                .build();
    }
}
