package com.ota_service.ota_service.dto.customer.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "고객 숙소 상세 조회 응답")
@Builder
public record CustomerAccommodationDetailResponse(
        @Schema(example = "ACC-000001")
        String accommodationCode,

        @Schema(example = "강남 시티 호텔")
        String name,

        @Schema(example = "DOMESTIC")
        AccommodationRegionType regionType,

        @Schema(example = "HOTEL_RESORT")
        AccommodationType accommodationType,

        @Schema(example = "KR-11-11680")
        String regionCode,

        @Schema(example = "대한민국 서울 강남구")
        String regionName,

        @Schema(example = "서울특별시 강남구 테헤란로 100")
        String address,

        @Schema(example = "https://cdn.ota.local/accommodations/1/thumb.jpg")
        String thumbnailImage,

        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOutTime,

        @Schema(example = "강남 중심에 위치한 비즈니스 호텔")
        String description,

        @Schema(example = "조식 유료 제공, 지하 주차장 이용 가능")
        String extraInfo,

        @Schema(example = "2026-04-10")
        LocalDate stayStartDate,

        @Schema(example = "2026-04-12")
        LocalDate stayEndDate,

        @Schema(example = "2")
        Integer guestCount,

        List<CustomerAccommodationImageResponse> images,

        List<CustomerAccommodationRoomResponse> rooms
) {
    public static CustomerAccommodationDetailResponse of(
            Accommodation accommodation,
            AccommodationDetail detail,
            Region region,
            LocalDate stayStartDate,
            LocalDate stayEndDate,
            Integer guestCount,
            List<CustomerAccommodationImageResponse> images,
            List<CustomerAccommodationRoomResponse> rooms
    ) {
        return CustomerAccommodationDetailResponse.builder()
                .accommodationCode(accommodation.getCode())
                .name(accommodation.getName())
                .regionType(accommodation.getRegionType())
                .accommodationType(accommodation.getAccommodationType())
                .regionCode(region == null ? null : region.getCode())
                .regionName(region == null ? null : region.getFullName())
                .address(accommodation.getAddress())
                .thumbnailImage(accommodation.getThumbnailImage())
                .checkInTime(accommodation.getCheckInTime())
                .checkOutTime(accommodation.getCheckOutTime())
                .description(detail == null ? null : detail.getDescription())
                .extraInfo(detail == null ? null : detail.getExtraInfo())
                .stayStartDate(stayStartDate)
                .stayEndDate(stayEndDate)
                .guestCount(guestCount)
                .images(images)
                .rooms(rooms)
                .build();
    }
}
