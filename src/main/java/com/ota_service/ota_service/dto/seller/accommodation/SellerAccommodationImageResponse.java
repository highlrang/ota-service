package com.ota_service.ota_service.dto.seller.accommodation;

import com.ota_service.ota_service.entity.AccommodationImage;
import com.ota_service.ota_service.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "숙소 이미지 응답")
@Builder
public record SellerAccommodationImageResponse(
        @Schema(description = "이미지 URL", example = "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        String imageUrl,
        @Schema(description = "이미지 유형", example = "PRIMARY")
        ImageType imageType,
        @Schema(description = "정렬 순서", example = "1")
        Integer sortOrder
) {
    public static SellerAccommodationImageResponse from(AccommodationImage accommodationImage) {
        return SellerAccommodationImageResponse.builder()
                .imageUrl(accommodationImage.getImageUrl())
                .imageType(accommodationImage.getImageType())
                .sortOrder(accommodationImage.getSortOrder())
                .build();
    }
}
