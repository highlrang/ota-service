package com.ota_service.ota_service.dto.supplier.onda;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OndaRateplanContent(
        String propertyId,
        String roomtypeId,
        String rateplanId,
        String name,
        BigDecimal basePrice,
        String currency,
        BigDecimal salePrice,
        Boolean refundable,
        LocalDate validFrom,
        LocalDate validTo
) {
}
