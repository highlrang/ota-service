package com.ota_service.ota_service.dto.extranet.room;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "판매자 객실 상세 응답")
@Builder
public record ExtranetRoomDetailResponse(
        @Schema(description = "객실 코드", example = "ROOM-000010")
        String roomCode,
        @Schema(description = "객실명", example = "디럭스 트윈")
        String name,
        @Schema(description = "객실 설명", example = "욕조가 포함된 트윈 객실")
        String description,
        @Schema(description = "기준 인원", example = "2")
        Integer standardOccupancy,
        @Schema(description = "최대 인원", example = "3")
        Integer maxOccupancy,
        @Schema(description = "침대 타입", example = "TWIN")
        BedType bedType,
        @Schema(description = "추가 정보", example = "엑스트라 베드 가능")
        String extraInfo,
        @Schema(description = "객실 기본 최소 숙박 일수", example = "1")
        Integer minStayNights,
        @Schema(description = "객실 기본 최대 숙박 일수", example = "5")
        Integer maxStayNights,
        @Schema(description = "객실 이미지 목록")
        List<ExtranetRoomImageResponse> images,
        @Schema(description = "객실 요금 override 목록")
        List<ExtranetRoomRateResponse> rates,
        @Schema(description = "객실 재고 목록")
        List<ExtranetRoomInventoryResponse> inventories,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "객실 등록 일시", example = "2026-04-03 14:20:00")
        LocalDateTime createdAt
) {
    public static ExtranetRoomDetailResponse of(
            Room room,
            List<ExtranetRoomImageResponse> images,
            List<ExtranetRoomRateResponse> rates,
            List<ExtranetRoomInventoryResponse> inventories,
            LocalDateTime createdAt
    ) {
        return ExtranetRoomDetailResponse.builder()
                .roomCode(room.getRoomCode())
                .name(room.getName())
                .description(room.getDescription())
                .standardOccupancy(room.getStandardOccupancy())
                .maxOccupancy(room.getMaxOccupancy())
                .bedType(room.getBedType())
                .extraInfo(room.getExtraInfo())
                .minStayNights(room.getMinStayNights())
                .maxStayNights(room.getMaxStayNights())
                .images(images)
                .rates(rates)
                .inventories(inventories)
                .createdAt(createdAt)
                .build();
    }
}
