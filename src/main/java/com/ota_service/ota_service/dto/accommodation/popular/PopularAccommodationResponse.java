package com.ota_service.ota_service.dto.accommodation.popular;

import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PopularAccommodationResponse(
        @Schema(example = "ACC-000001")
        String accommodationCode,

        @Schema(example = "강남 시티 호텔")
        String name,

        @Schema(example = "HOTEL_RESORT")
        String accommodationType,

        @Schema(example = "KR-11")
        String regionCode,

        @Schema(example = "대한민국 서울")
        String regionName,

        @Schema(example = "서울특별시 강남구 테헤란로 100")
        String address,

        @Schema(example = "https://cdn.ota.local/accommodations/1/thumb.jpg")
        String thumbnailImage,

        @Schema(example = "135000")
        BigDecimal displayPrice,

        @Schema(example = "KRW")
        String currency
) {
    public static PopularAccommodationResponse of(
            Accommodation accommodation,
            Region region,
            BigDecimal displayPrice,
            String currency
    ) {
        return PopularAccommodationResponse.builder()
                .accommodationCode(accommodation.getCode())
                .name(accommodation.getName())
                .accommodationType(accommodation.getAccommodationType().name())
                .regionCode(region == null ? null : region.getCode())
                .regionName(region == null ? null : region.getFullName())
                .address(accommodation.getAddress())
                .thumbnailImage(accommodation.getThumbnailImage())
                .displayPrice(displayPrice)
                .currency(currency)
                .build();
    }
}
