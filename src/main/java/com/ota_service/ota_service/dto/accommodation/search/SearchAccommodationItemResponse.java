package com.ota_service.ota_service.dto.accommodation.search;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SearchAccommodationItemResponse(
        @Schema(example = "ACC-000001")
        String accommodationCode,

        @Schema(example = "강남 시티 호텔")
        String name,

        @Schema(example = "HOTEL_RESORT")
        String accommodationType,

        @Schema(example = "KR-11-11680")
        String regionCode,

        @Schema(example = "대한민국 서울 강남구")
        String regionName,

        @Schema(example = "서울특별시 강남구 테헤란로 100")
        String address,

        @Schema(example = "https://cdn.ota.local/accommodations/1/thumb.jpg")
        String thumbnailImage,

        LocalTime checkInTime,
        LocalTime checkOutTime,

        @Schema(example = "2026-04-10")
        LocalDate stayStartDate,

        @Schema(example = "2026-04-12")
        LocalDate stayEndDate,

        @Schema(example = "270000")
        BigDecimal minTotalAmount,

        @Schema(example = "KRW")
        String currency,

        @Schema(example = "1")
        Integer availableRoomCount,

        @Schema(example = "2")
        Integer maxGuestCount,

        @Schema(example = "false")
        boolean soldOut
) {
}
