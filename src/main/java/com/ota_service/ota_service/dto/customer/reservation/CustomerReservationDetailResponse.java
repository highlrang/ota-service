package com.ota_service.ota_service.dto.customer.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.enums.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "고객 예약 상세 조회 응답")
@Builder
public record CustomerReservationDetailResponse(
        @Schema(example = "RSV-260405-0001")
        String reservationNo,

        @Schema(example = "CONFIRMED")
        String reservationStatus,

        @Schema(example = "ACC-000001")
        String accommodationCode,

        @Schema(example = "강남 시티 호텔")
        String accommodationName,

        @Schema(example = "HOTEL_RESORT")
        AccommodationType accommodationType,

        @Schema(example = "2026-04-10 15:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkInAt,

        @Schema(example = "2026-04-12 11:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkOutAt,

        @Schema(example = "김여행")
        String guestName,

        @Schema(example = "010-1234-5678")
        String guestPhoneNumber,

        @Schema(example = "2")
        Integer adultCount,

        @Schema(example = "0")
        Integer childCount,

        @Schema(example = "270000")
        java.math.BigDecimal totalAmount,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime confirmedAt,

        CustomerReservationPaymentResponse payment
) {
    public static CustomerReservationDetailResponse of(
            Reservation reservation,
            Accommodation accommodation,
            CustomerReservationPaymentResponse payment
    ) {
        return CustomerReservationDetailResponse.builder()
                .reservationNo(reservation.getReservationNo())
                .reservationStatus(reservation.getReservationStatus().name())
                .accommodationCode(accommodation.getCode())
                .accommodationName(accommodation.getName())
                .accommodationType(accommodation.getAccommodationType())
                .checkInAt(reservation.getCheckInAt())
                .checkOutAt(reservation.getCheckOutAt())
                .guestName(reservation.getGuestName())
                .guestPhoneNumber(reservation.getGuestPhoneNumber())
                .adultCount(reservation.getAdultCount())
                .childCount(reservation.getChildCount())
                .totalAmount(reservation.getTotalAmount())
                .confirmedAt(reservation.getConfirmedAt())
                .payment(payment)
                .build();
    }
}
