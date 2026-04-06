package com.ota_service.ota_service.dto.supplier.onda;

import com.ota_service.ota_service.enums.BedType;
import java.math.BigDecimal;

public record OndaRoomtypeContent(
        String propertyId,
        String roomtypeId,
        String name,
        String description,
        Integer standardOccupancy,
        Integer maxOccupancy,
        BedType bedType,
        String extraInfo,
        BigDecimal basePrice,
        String currency,
        BigDecimal salePrice,
        Boolean refundable,
        Integer minStayNights,
        Integer maxStayNights,
        Integer defaultStock
) {
}
