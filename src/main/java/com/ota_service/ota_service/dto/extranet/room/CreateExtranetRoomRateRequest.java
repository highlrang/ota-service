package com.ota_service.ota_service.dto.extranet.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "기간별 객실 요금 override 저장 요청")
public record CreateExtranetRoomRateRequest(
        @Schema(description = "요금명", example = "주말 특가")
        @NotBlank(message = "요금명은 필수입니다.")
        String rateName,

        @Schema(description = "기본 요금", example = "220000.00")
        @NotNull(message = "기본 요금은 필수입니다.")
        @DecimalMin(value = "0.00", message = "기본 요금은 0 이상이어야 합니다.")
        BigDecimal basePrice,

        @Schema(description = "통화", example = "KRW")
        @NotBlank(message = "통화는 필수입니다.")
        String currency,

        @Schema(description = "판매 요금", example = "198000.00")
        @NotNull(message = "판매 요금은 필수입니다.")
        @DecimalMin(value = "0.00", message = "판매 요금은 0 이상이어야 합니다.")
        BigDecimal salePrice,

        @Schema(description = "환불 가능 여부", example = "false")
        @NotNull(message = "환불 가능 여부는 필수입니다.")
        Boolean refundable,

        @Schema(description = "적용 시작일", example = "2026-04-01")
        @NotNull(message = "적용 시작일은 필수입니다.")
        LocalDate validFrom,

        @Schema(description = "적용 종료일", example = "2026-12-31")
        @NotNull(message = "적용 종료일은 필수입니다.")
        LocalDate validTo
) {
}
