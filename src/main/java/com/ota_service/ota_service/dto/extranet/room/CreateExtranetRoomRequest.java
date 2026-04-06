package com.ota_service.ota_service.dto.extranet.room;

import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "판매자 객실 등록 요청")
public record CreateExtranetRoomRequest(
        @Schema(description = "객실명", example = "디럭스 트윈")
        @NotBlank(message = "객실명은 필수입니다.")
        String name,

        @Schema(description = "객실 설명", example = "욕조가 포함된 트윈 객실")
        String description,

        @Schema(description = "기준 인원", example = "2")
        @NotNull(message = "기준 인원은 필수입니다.")
        @Min(value = 1, message = "기준 인원은 1 이상이어야 합니다.")
        Integer standardOccupancy,

        @Schema(description = "최대 인원", example = "3")
        @NotNull(message = "최대 인원은 필수입니다.")
        @Min(value = 1, message = "최대 인원은 1 이상이어야 합니다.")
        Integer maxOccupancy,

        @Schema(description = "침대 타입", example = "TWIN")
        BedType bedType,

        @Schema(description = "추가 정보", example = "엑스트라 베드 가능")
        String extraInfo,

        @Schema(description = "객실 기본 최소 숙박 일수", example = "1")
        @NotNull(message = "객실 기본 최소 숙박 일수는 필수입니다.")
        @Min(value = 1, message = "객실 기본 최소 숙박 일수는 1 이상이어야 합니다.")
        Integer minStayNights,

        @Schema(description = "객실 기본 최대 숙박 일수", example = "5")
        @NotNull(message = "객실 기본 최대 숙박 일수는 필수입니다.")
        @Min(value = 1, message = "객실 기본 최대 숙박 일수는 1 이상이어야 합니다.")
        Integer maxStayNights,

        @Schema(description = "객실 이미지 목록")
        @Valid
        @NotEmpty(message = "객실 이미지는 최소 1개 이상이어야 합니다.")
        List<CreateExtranetRoomImageRequest> images,

        @Schema(description = "기간별 객실 재고 설정 목록")
        @Valid
        @NotEmpty(message = "객실 재고는 최소 1개 이상이어야 합니다.")
        List<CreateExtranetRoomInventoryRequest> inventories,

        @Schema(description = "기간별 객실 요금 설정 목록")
        @Valid
        @NotEmpty(message = "객실 요금은 최소 1개 이상이어야 합니다.")
        List<CreateExtranetRoomRateRequest> rates
) {
}
