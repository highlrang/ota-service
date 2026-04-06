package com.ota_service.ota_service.dto.extranet.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "판매자 객실 재고 수정 요청")
public record UpdateExtranetRoomInventoriesRequest(
        @Schema(description = "객실 재고 목록")
        @Valid
        @NotNull(message = "객실 재고 목록은 필수입니다.")
        List<CreateExtranetRoomInventoryRequest> inventories
) {
}
