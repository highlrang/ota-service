package com.ota_service.ota_service.dto.extranet.room;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "판매자 객실 등록 응답")
@Builder
public record CreateExtranetRoomResponse(
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
        @Schema(description = "객실 기본 기준가", example = "220000.00")
        BigDecimal basePrice,
        @Schema(description = "객실 기본 통화", example = "KRW")
        String currency,
        @Schema(description = "객실 기본 판매가", example = "198000.00")
        BigDecimal salePrice,
        @Schema(description = "객실 기본 환불 가능 여부", example = "false")
        Boolean refundable,
        @Schema(description = "객실 기본 최소 숙박 일수", example = "1")
        Integer minStayNights,
        @Schema(description = "객실 기본 최대 숙박 일수", example = "5")
        Integer maxStayNights,
        @Schema(description = "객실 기본 재고", example = "5")
        Integer defaultStock,
        @Schema(description = "객실 이미지 목록")
        List<ExtranetRoomImageResponse> images,
        @Schema(description = "객실 재고 목록")
        List<ExtranetRoomInventoryResponse> inventories,
        @Schema(description = "객실 요금 목록")
        List<ExtranetRoomRateResponse> rates,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "객실 생성일시", example = "2026-04-03 14:20:00")
        LocalDateTime createdAt
) {
    public static CreateExtranetRoomResponse of(
            Room room,
            List<ExtranetRoomImageResponse> images,
            List<ExtranetRoomInventoryResponse> inventories,
            List<ExtranetRoomRateResponse> rates,
            LocalDateTime createdAt
    ) {
        return CreateExtranetRoomResponse.builder()
                .roomCode(room.getRoomCode())
                .name(room.getName())
                .description(room.getDescription())
                .standardOccupancy(room.getStandardOccupancy())
                .maxOccupancy(room.getMaxOccupancy())
                .bedType(room.getBedType())
                .extraInfo(room.getExtraInfo())
                .basePrice(room.getBasePrice())
                .currency(room.getCurrency())
                .salePrice(room.getSalePrice())
                .refundable(room.getRefundable())
                .minStayNights(room.getMinStayNights())
                .maxStayNights(room.getMaxStayNights())
                .defaultStock(room.getDefaultStock())
                .images(images)
                .inventories(inventories)
                .rates(rates)
                .createdAt(createdAt)
                .build();
    }
}
