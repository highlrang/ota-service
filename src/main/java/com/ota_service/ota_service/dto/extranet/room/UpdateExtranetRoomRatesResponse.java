package com.ota_service.ota_service.dto.extranet.room;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "객실 요금 수정 응답")
@Builder
public record UpdateExtranetRoomRatesResponse(
        @Schema(description = "객실 코드", example = "ROOM-000001")
        String roomCode,
        @Schema(description = "객실 요금 override 목록")
        List<ExtranetRoomRateResponse> rates
) {
    public static UpdateExtranetRoomRatesResponse of(String roomCode, List<ExtranetRoomRateResponse> rates) {
        return UpdateExtranetRoomRatesResponse.builder()
                .roomCode(roomCode)
                .rates(rates)
                .build();
    }
}
