package com.ota_service.ota_service.dto.extranet.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.AccommodationType;
import com.ota_service.ota_service.enums.BusinessStatus;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record CreateExtranetAccommodationResponse(
        String code,
        String name,
        AccommodationRegionType regionType,
        AccommodationType accommodationType,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String thumbnailImage,
        BusinessStatus businessStatus,
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInTime,
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOutTime,
        String description,
        String extraInfo
) {
    public static CreateExtranetAccommodationResponse of(Accommodation accommodation, AccommodationDetail detail) {
        return CreateExtranetAccommodationResponse.builder()
                .code(accommodation.getCode())
                .name(accommodation.getName())
                .regionType(accommodation.getRegionType())
                .accommodationType(accommodation.getAccommodationType())
                .address(accommodation.getAddress())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .thumbnailImage(accommodation.getThumbnailImage())
                .businessStatus(accommodation.getBusinessStatus())
                .checkInTime(accommodation.getCheckInTime())
                .checkOutTime(accommodation.getCheckOutTime())
                .description(detail.getDescription())
                .extraInfo(detail.getExtraInfo())
                .build();
    }
}
