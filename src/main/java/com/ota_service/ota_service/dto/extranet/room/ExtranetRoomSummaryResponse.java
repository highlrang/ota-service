package com.ota_service.ota_service.dto.extranet.room;

import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "숙소 상세 내 객실 요약 응답")
@Builder
public record ExtranetRoomSummaryResponse(
        @Schema(description = "객실 코드", example = "ROOM-000001")
        String roomCode,
        @Schema(description = "객실명", example = "디럭스 트윈")
        String name,
        @Schema(description = "침구 타입", example = "TWIN")
        BedType bedType,
        @Schema(description = "기준 인원", example = "2")
        Integer standardOccupancy,
        @Schema(description = "최대 인원", example = "3")
        Integer maxOccupancy
) {
    public static ExtranetRoomSummaryResponse from(Room room) {
        return ExtranetRoomSummaryResponse.builder()
                .roomCode(room.getRoomCode())
                .name(room.getName())
                .bedType(room.getBedType())
                .standardOccupancy(room.getStandardOccupancy())
                .maxOccupancy(room.getMaxOccupancy())
                .build();
    }
}
