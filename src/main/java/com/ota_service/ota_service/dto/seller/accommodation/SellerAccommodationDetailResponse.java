package com.ota_service.ota_service.dto.seller.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.dto.seller.room.SellerRoomSummaryResponse;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.SellerAccommodation;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.AccommodationType;
import com.ota_service.ota_service.enums.BusinessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "판매자 숙소 상세 조회 응답")
@Builder
public record SellerAccommodationDetailResponse(
        @Schema(description = "숙소 코드", example = "ACC-000001")
        String code,
        @Schema(description = "숙소명", example = "강남 시티 호텔")
        String name,
        @Schema(description = "지역 유형", example = "DOMESTIC")
        AccommodationRegionType regionType,
        @Schema(description = "숙소 유형", example = "HOTEL_RESORT")
        AccommodationType accommodationType,
        @Schema(description = "표시 주소", example = "서울특별시 강남구 테헤란로 100")
        String address,
        @Schema(description = "썸네일 이미지 URL", example = "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        String thumbnailImage,
        @Schema(description = "영업 상태", example = "OPEN")
        BusinessStatus businessStatus,
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "체크인 시간", example = "15:00")
        LocalTime checkInTime,
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "체크아웃 시간", example = "11:00")
        LocalTime checkOutTime,
        @Schema(description = "숙소 상세 설명", example = "강남 중심에 위치한 비즈니스 호텔")
        String description,
        @Schema(description = "숙소 추가 정보", example = "조식 유료 제공, 지하 주차장 이용 가능")
        String extraInfo,
        @Schema(description = "객실 요약 목록")
        List<SellerRoomSummaryResponse> rooms,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "판매자 상품 등록 일시", example = "2026-04-03 10:15:30")
        LocalDateTime createdAt
) {
    public static SellerAccommodationDetailResponse of(
            Accommodation accommodation,
            AccommodationDetail detail,
            List<SellerRoomSummaryResponse> rooms,
            SellerAccommodation sellerAccommodation
    ) {
        return SellerAccommodationDetailResponse.builder()
                .code(accommodation.getCode())
                .name(accommodation.getName())
                .regionType(accommodation.getRegionType())
                .accommodationType(accommodation.getAccommodationType())
                .address(accommodation.getAddress())
                .thumbnailImage(accommodation.getThumbnailImage())
                .businessStatus(accommodation.getBusinessStatus())
                .checkInTime(accommodation.getCheckInTime())
                .checkOutTime(accommodation.getCheckOutTime())
                .description(detail == null ? null : detail.getDescription())
                .extraInfo(detail == null ? null : detail.getExtraInfo())
                .rooms(rooms)
                .createdAt(sellerAccommodation.getCreatedAt())
                .build();
    }
}
