package com.ota_service.ota_service.dto.extranet.room;

import com.ota_service.ota_service.entity.RoomInventory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Schema(description = "객실 재고 응답")
@Builder
public record ExtranetRoomInventoryResponse(
        @Schema(description = "재고 일자", example = "2026-04-20")
        LocalDate inventoryDate,
        @Schema(description = "전체 재고 수량", example = "5")
        Integer totalStock,
        @Schema(description = "예약된 수량", example = "1")
        Integer reservedStock,
        @Schema(description = "판매 가능 수량", example = "4")
        Integer availableStock,
        @Schema(description = "판매 중지 여부", example = "false")
        Boolean stopSale
) {
    public static ExtranetRoomInventoryResponse from(RoomInventory roomInventory) {
        return ExtranetRoomInventoryResponse.builder()
                .inventoryDate(roomInventory.getInventoryDate())
                .totalStock(roomInventory.getTotalStock())
                .reservedStock(roomInventory.getReservedStock())
                .availableStock(roomInventory.getAvailableStock())
                .stopSale(roomInventory.getStopSale())
                .build();
    }
}
