package com.ota_service.ota_service.dto.seller.room;

import com.ota_service.ota_service.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "객실 이미지 저장 요청")
public record CreateSellerRoomImageRequest(
        @Schema(description = "이미지 URL", example = "/uploads/room-images/6f7d9d0e-main.jpg")
        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @Schema(description = "이미지 유형", example = "PRIMARY")
        @NotNull(message = "이미지 유형은 필수입니다.")
        ImageType imageType,

        @Schema(description = "정렬 순서", example = "1")
        @NotNull(message = "정렬 순서는 필수입니다.")
        @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
        Integer sortOrder
) {
}
