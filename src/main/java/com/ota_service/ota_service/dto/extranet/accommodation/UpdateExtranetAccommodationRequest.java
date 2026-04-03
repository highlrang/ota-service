package com.ota_service.ota_service.dto.extranet.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.enums.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

@Schema(description = "판매자 숙소 정보 수정 요청")
public record UpdateExtranetAccommodationRequest(
        @Schema(description = "숙소명", example = "강남 시티 호텔")
        @Size(max = 255, message = "숙소명은 255자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "숙소 타입", example = "HOTEL_RESORT")
        AccommodationType accommodationType,

        @Schema(description = "주소 정보")
        @Valid
        AddressInfo addressInfo,

        @Schema(description = "썸네일 이미지 URL", example = "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        @Size(max = 255, message = "썸네일 이미지는 255자를 초과할 수 없습니다.")
        String thumbnailImage,

        @Schema(description = "체크인 시간", example = "15:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInTime,

        @Schema(description = "체크아웃 시간", example = "11:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOutTime,

        @Schema(description = "숙소 설명", example = "강남 중심에 위치한 비즈니스 호텔")
        String description,

        @Schema(description = "기타 정보", example = "조식 유료 제공, 지하 주차장 이용 가능")
        @Size(max = 1000, message = "기타 정보는 1000자를 초과할 수 없습니다.")
        String extraInfo
) {
}
