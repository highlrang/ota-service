package com.ota_service.ota_service.dto.customer.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.enums.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "고객 예약 목록 조회 응답 항목")
@Builder
public record CustomerReservationItemResponse(
        @Schema(example = "RSV-260405-0001")
        String reservationNo,

        @Schema(example = "강남 시티 호텔")
        String accommodationName,

        @Schema(example = "HOTEL_RESORT")
        AccommodationType accommodationType,

        @Schema(example = "CONFIRMED")
        String reservationStatus,

        @Schema(example = "2026-04-10 15:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkInAt,

        @Schema(example = "2026-04-12 11:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkOutAt
) {
    public static CustomerReservationItemResponse of(Reservation reservation, Accommodation accommodation) {
        return CustomerReservationItemResponse.builder()
                .reservationNo(reservation.getReservationNo())
                .accommodationName(accommodation.getName())
                .accommodationType(accommodation.getAccommodationType())
                .reservationStatus(reservation.getReservationStatus().name())
                .checkInAt(reservation.getCheckInAt())
                .checkOutAt(reservation.getCheckOutAt())
                .build();
    }
}
