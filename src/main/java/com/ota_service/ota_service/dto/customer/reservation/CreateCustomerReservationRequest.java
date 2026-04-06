package com.ota_service.ota_service.dto.customer.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCustomerReservationRequest(
        @Schema(example = "김여행")
        @NotBlank
        String guestName,

        @Schema(example = "010-1234-5678")
        @NotBlank
        String guestPhoneNumber,

        @Schema(example = "ACC-000001")
        @NotBlank
        String accommodationCode,

        @Schema(example = "ROOM-SEOUL-0001")
        @NotBlank
        String roomCode,

        @Schema(example = "2026-04-10 15:00", description = "yyyy-MM-dd HH:mm 기준 체크인 일시")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @NotNull
        @FutureOrPresent
        LocalDateTime checkInAt,

        @Schema(example = "2026-04-12 11:00", description = "yyyy-MM-dd HH:mm 기준 체크아웃 일시")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @NotNull
        LocalDateTime checkOutAt,

        @Schema(example = "CARD")
        @NotBlank
        String paymentMethod,

        @Schema(example = "270000")
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal paymentAmount,

        @Schema(example = "2", description = "미입력 시 1명으로 처리됩니다.")
        @Min(0)
        Integer adultCount,

        @Schema(example = "0", description = "미입력 시 0명으로 처리됩니다.")
        @Min(0)
        Integer childCount
) {
}
