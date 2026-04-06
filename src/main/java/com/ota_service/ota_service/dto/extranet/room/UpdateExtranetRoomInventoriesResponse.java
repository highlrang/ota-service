package com.ota_service.ota_service.dto.extranet.room;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "객실 재고 수정 응답")
@Builder
public record UpdateExtranetRoomInventoriesResponse(
        @Schema(description = "객실 코드", example = "ROOM-000001")
        String roomCode,
        @Schema(description = "객실 재고 목록")
        List<ExtranetRoomInventoryResponse> inventories
) {
    public static UpdateExtranetRoomInventoriesResponse of(String roomCode, List<ExtranetRoomInventoryResponse> inventories) {
        return UpdateExtranetRoomInventoriesResponse.builder()
                .roomCode(roomCode)
                .inventories(inventories)
                .build();
    }
}
