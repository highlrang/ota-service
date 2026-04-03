package com.ota_service.ota_service.dto.customer.accommodation;

import com.ota_service.ota_service.entity.AccommodationImage;
import com.ota_service.ota_service.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "고객 숙소 이미지 응답")
@Builder
public record CustomerAccommodationImageResponse(
        @Schema(example = "https://cdn.ota.local/accommodations/1/main.jpg")
        String imageUrl,

        @Schema(example = "GENERAL")
        ImageType imageType,

        @Schema(example = "1")
        Integer sortOrder
) {
    public static CustomerAccommodationImageResponse from(AccommodationImage accommodationImage) {
        return CustomerAccommodationImageResponse.builder()
                .imageUrl(accommodationImage.getImageUrl())
                .imageType(accommodationImage.getImageType())
                .sortOrder(accommodationImage.getSortOrder())
                .build();
    }
}
