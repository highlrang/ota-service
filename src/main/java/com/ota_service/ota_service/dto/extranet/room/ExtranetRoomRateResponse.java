package com.ota_service.ota_service.dto.extranet.room;

import com.ota_service.ota_service.entity.RoomRate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Schema(description = "객실 요금 응답")
@Builder
public record ExtranetRoomRateResponse(
        @Schema(description = "요금명", example = "디럭스 특가")
        String rateName,
        @Schema(description = "기본 요금", example = "220000.00")
        BigDecimal basePrice,
        @Schema(description = "통화", example = "KRW")
        String currency,
        @Schema(description = "판매 요금", example = "198000.00")
        BigDecimal salePrice,
        @Schema(description = "환불 가능 여부", example = "false")
        Boolean refundable,
        @Schema(description = "요금 일자", example = "2026-04-01")
        LocalDate rateDate
) {
    public static ExtranetRoomRateResponse from(RoomRate roomRate) {
        return ExtranetRoomRateResponse.builder()
                .rateName(roomRate.getRateName())
                .basePrice(roomRate.getBasePrice())
                .currency(roomRate.getCurrency())
                .salePrice(roomRate.getSalePrice())
                .refundable(roomRate.getRefundable())
                .rateDate(roomRate.getRateDate())
                .build();
    }

}
