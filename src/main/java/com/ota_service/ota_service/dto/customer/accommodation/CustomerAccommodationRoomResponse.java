package com.ota_service.ota_service.dto.customer.accommodation;

import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Schema(description = "예약 가능한 단위 기준 고객 객실 응답")
@Builder
public record CustomerAccommodationRoomResponse(
        @Schema(example = "ROOM-000001")
        String roomCode,

        @Schema(example = "디럭스 트윈")
        String name,

        @Schema(example = "욕조가 포함된 트윈 객실")
        String description,

        @Schema(example = "2")
        Integer standardOccupancy,

        @Schema(example = "3")
        Integer maxOccupancy,

        @Schema(example = "TWIN")
        BedType bedType,

        @Schema(example = "엑스트라 베드 가능")
        String extraInfo,

        @Schema(example = "1")
        Integer minStayNights,

        @Schema(example = "5")
        Integer maxStayNights,

        @Schema(example = "240000")
        BigDecimal totalAmount,

        @Schema(example = "KRW")
        String currency,

        @Schema(example = "true")
        boolean refundable,

        @Schema(example = "1")
        Integer availableStock,

        @Schema(example = "false")
        boolean soldOut,

        List<CustomerRoomImageResponse> images
) {
    public static CustomerAccommodationRoomResponse of(
            Room room,
            BigDecimal totalAmount,
            String currency,
            boolean refundable,
            Integer availableStock,
            boolean soldOut,
            List<CustomerRoomImageResponse> images
    ) {
        return CustomerAccommodationRoomResponse.builder()
                .roomCode(room.getRoomCode())
                .name(room.getName())
                .description(room.getDescription())
                .standardOccupancy(room.getStandardOccupancy())
                .maxOccupancy(room.getMaxOccupancy())
                .bedType(room.getBedType())
                .extraInfo(room.getExtraInfo())
                .minStayNights(room.getMinStayNights())
                .maxStayNights(room.getMaxStayNights())
                .totalAmount(totalAmount)
                .currency(currency)
                .refundable(refundable)
                .availableStock(availableStock)
                .soldOut(soldOut)
                .images(images)
                .build();
    }
}
