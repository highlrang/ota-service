package com.ota_service.ota_service.dto.seller.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "객실 이미지 업로드 응답")
@Builder
public record RoomImageUploadResponse(
        @Schema(description = "업로드된 파일명", example = "0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        String fileName,
        @Schema(description = "클라이언트가 객실 등록 API에 넣을 공개 URL", example = "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        String imageUrl
) {
    public static RoomImageUploadResponse of(String fileName, String imageUrl) {
        return RoomImageUploadResponse.builder()
                .fileName(fileName)
                .imageUrl(imageUrl)
                .build();
    }
}
