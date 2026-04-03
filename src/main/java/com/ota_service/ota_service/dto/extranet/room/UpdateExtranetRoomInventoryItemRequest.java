package com.ota_service.ota_service.dto.extranet.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "판매자 객실 재고 수정 항목")
public record UpdateExtranetRoomInventoryItemRequest(
        @Schema(description = "재고 일자", example = "2026-04-20")
        @NotNull(message = "재고 일자는 필수입니다.")
        LocalDate inventoryDate,

        @Schema(description = "전체 재고 수량", example = "5")
        @NotNull(message = "전체 재고 수량은 필수입니다.")
        @Min(value = 0, message = "전체 재고 수량은 0 이상이어야 합니다.")
        Integer totalStock,

        @Schema(description = "판매 중지 여부", example = "false")
        @NotNull(message = "판매 중지 여부는 필수입니다.")
        Boolean stopSale
) {
}
