package com.ota_service.ota_service.dto.extranet.room;

import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "판매자 객실 정보 수정 요청")
public record UpdateExtranetRoomRequest(
        @Schema(description = "객실명", example = "디럭스 트윈")
        @Size(max = 255, message = "객실명은 255자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "객실 설명", example = "욕조가 포함된 트윈 객실")
        String description,

        @Schema(description = "기준 인원", example = "2")
        @Min(value = 1, message = "기준 인원은 1 이상이어야 합니다.")
        Integer standardOccupancy,

        @Schema(description = "최대 인원", example = "3")
        @Min(value = 1, message = "최대 인원은 1 이상이어야 합니다.")
        Integer maxOccupancy,

        @Schema(description = "침대 타입", example = "TWIN")
        BedType bedType,

        @Schema(description = "추가 정보", example = "엑스트라 베드 가능")
        String extraInfo,

        @Schema(description = "객실 기본 최소 숙박 일수", example = "1")
        @Min(value = 1, message = "객실 기본 최소 숙박 일수는 1 이상이어야 합니다.")
        Integer minStayNights,

        @Schema(description = "객실 기본 최대 숙박 일수", example = "5")
        @Min(value = 1, message = "객실 기본 최대 숙박 일수는 1 이상이어야 합니다.")
        Integer maxStayNights
) {
}
