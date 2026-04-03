package com.ota_service.ota_service.dto.supplier.onda;

import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.AccommodationType;
import java.math.BigDecimal;
import java.time.LocalTime;

public record OndaPropertyContent(
        String propertyId,
        String name,
        AccommodationRegionType regionType,
        AccommodationType accommodationType,
        Long regionId,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String thumbnailImage,
        String description,
        String extraInfo,
        LocalTime checkInTime,
        LocalTime checkOutTime
) {
}
