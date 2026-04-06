package com.ota_service.ota_service.dto.extranet.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.entity.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "판매자 예약 목록 조회 응답 항목")
@Builder
public record ExtranetReservationItemResponse(
        @Schema(example = "RSV-260405-0001")
        String reservationNo,

        @Schema(example = "ROOM-SEOUL-0001")
        String roomCode,

        @Schema(example = "스탠다드 더블")
        String roomName,

        @Schema(example = "김여행")
        String guestName,

        @Schema(example = "010-1111-2222")
        String guestPhoneNumber,

        @Schema(example = "CONFIRMED")
        String reservationStatus,

        @Schema(example = "270000.00")
        BigDecimal totalAmount,

        @Schema(example = "2026-04-10 15:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkInAt,

        @Schema(example = "2026-04-12 11:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkOutAt,

        @Schema(example = "2026-04-01 09:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime createdAt
) {
    public static ExtranetReservationItemResponse of(Reservation reservation, Room room) {
        return ExtranetReservationItemResponse.builder()
                .reservationNo(reservation.getReservationNo())
                .roomCode(room.getRoomCode())
                .roomName(room.getName())
                .guestName(reservation.getGuestName())
                .guestPhoneNumber(reservation.getGuestPhoneNumber())
                .reservationStatus(reservation.getReservationStatus().name())
                .totalAmount(reservation.getTotalAmount())
                .checkInAt(reservation.getCheckInAt())
                .checkOutAt(reservation.getCheckOutAt())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
