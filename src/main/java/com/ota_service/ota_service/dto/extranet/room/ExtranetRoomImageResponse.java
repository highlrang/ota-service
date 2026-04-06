package com.ota_service.ota_service.dto.extranet.room;

import com.ota_service.ota_service.entity.RoomImage;
import com.ota_service.ota_service.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "객실 이미지 응답")
@Builder
public record ExtranetRoomImageResponse(
        @Schema(description = "이미지 URL", example = "/uploads/room-images/6f7d9d0e-main.jpg")
        String imageUrl,
        @Schema(description = "이미지 유형", example = "PRIMARY")
        ImageType imageType,
        @Schema(description = "정렬 순서", example = "1")
        Integer sortOrder
) {
    public static ExtranetRoomImageResponse from(RoomImage roomImage) {
        return ExtranetRoomImageResponse.builder()
                .imageUrl(roomImage.getImageUrl())
                .imageType(roomImage.getImageType())
                .sortOrder(roomImage.getSortOrder())
                .build();
    }
}
