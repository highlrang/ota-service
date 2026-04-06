package com.ota_service.ota_service.dto.supplier.onda;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OndaInventoryItem(
        String propertyId,
        String roomtypeId,
        String rateplanId,
        LocalDate date,
        BigDecimal basicPrice,
        BigDecimal salePrice,
        BigDecimal extraAdult,
        BigDecimal extraChild,
        BigDecimal extraInfant,
        String promotionType,
        Integer vacancy
) {
}
