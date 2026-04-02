package com.ota_service.ota_service.dto.seller.room;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "객실 이미지 수정 응답")
@Builder
public record UpdateSellerRoomImagesResponse(
        @Schema(description = "객실 코드", example = "ROOM-000001")
        String roomCode,
        @Schema(description = "객실 이미지 목록")
        List<SellerRoomImageResponse> images
) {
    public static UpdateSellerRoomImagesResponse of(String roomCode, List<SellerRoomImageResponse> images) {
        return UpdateSellerRoomImagesResponse.builder()
                .roomCode(roomCode)
                .images(images)
                .build();
    }
}
