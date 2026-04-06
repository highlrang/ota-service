package com.ota_service.ota_service.dto.extranet.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "객실 재고 저장 요청")
public record CreateExtranetRoomInventoryRequest(
        @Schema(description = "적용 시작일", example = "2026-04-20")
        @NotNull(message = "적용 시작일은 필수입니다.")
        LocalDate validFrom,

        @Schema(description = "적용 종료일", example = "2026-04-30")
        @NotNull(message = "적용 종료일은 필수입니다.")
        LocalDate validTo,

        @Schema(description = "전체 재고 수량", example = "5")
        @NotNull(message = "전체 재고 수량은 필수입니다.")
        @Min(value = 0, message = "전체 재고 수량은 0 이상이어야 합니다.")
        Integer totalStock,

        @Schema(description = "판매 중지 여부", example = "false")
        @NotNull(message = "판매 중지 여부는 필수입니다.")
        Boolean stopSale
) {
}
