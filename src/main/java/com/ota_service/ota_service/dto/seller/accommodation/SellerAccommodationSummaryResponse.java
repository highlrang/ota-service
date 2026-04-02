package com.ota_service.ota_service.dto.seller.accommodation;

import com.ota_service.ota_service.enums.BusinessStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자가 등록한 숙소 목록 응답")
public record SellerAccommodationSummaryResponse(
        @Schema(description = "숙소 코드", example = "ACC-000001")
        String code,
        @Schema(description = "숙소명", example = "강남 시티 호텔")
        String name,
        @Schema(description = "표시 주소", example = "서울특별시 강남구 테헤란로 100")
        String address,
        @Schema(description = "대표 이미지 URL", example = "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        String thumbnailImage,
        @Schema(description = "영업 상태", example = "OPEN")
        BusinessStatus businessStatus
) {
}
