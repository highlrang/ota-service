package com.ota_service.ota_service.dto.customer.accommodation;

import com.ota_service.ota_service.entity.RoomImage;
import com.ota_service.ota_service.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "고객 객실 이미지 응답")
@Builder
public record CustomerRoomImageResponse(
        @Schema(example = "https://cdn.ota.local/rooms/1/main.jpg")
        String imageUrl,

        @Schema(example = "PRIMARY")
        ImageType imageType,

        @Schema(example = "1")
        Integer sortOrder
) {
    public static CustomerRoomImageResponse from(RoomImage roomImage) {
        return CustomerRoomImageResponse.builder()
                .imageUrl(roomImage.getImageUrl())
                .imageType(roomImage.getImageType())
                .sortOrder(roomImage.getSortOrder())
                .build();
    }
}
