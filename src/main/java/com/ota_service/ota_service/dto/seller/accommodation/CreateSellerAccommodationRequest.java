package com.ota_service.ota_service.dto.seller.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ota_service.ota_service.enums.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public record CreateSellerAccommodationRequest(
        @Schema(description = "숙소명", example = "강남 시티 호텔")
        @NotBlank(message = "숙소명은 필수입니다.")
        @Size(max = 255, message = "숙소명은 255자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "숙소 타입", example = "HOTEL_RESORT")
        @NotNull(message = "숙소 타입은 필수입니다.")
        AccommodationType accommodationType,

        @Schema(description = "외부 지도 API에서 받은 주소 정보")
        @Valid
        @NotNull(message = "주소 정보는 필수입니다.")
        AddressInfo addressInfo,

        @Schema(description = "이미지 업로드 API에서 받은 썸네일 imageUrl", example = "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg")
        @Size(max = 255, message = "썸네일 이미지는 255자를 초과할 수 없습니다.")
        String thumbnailImage,

        @Schema(description = "체크인 시간", example = "15:00")
        @NotNull(message = "체크인 시간은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInTime,

        @Schema(description = "체크아웃 시간", example = "11:00")
        @NotNull(message = "체크아웃 시간은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOutTime,

        @Schema(description = "숙소 설명", example = "강남 중심에 위치한 비즈니스 호텔")
        String description,

        @Schema(description = "기타 정보", example = "조식 유료 제공, 지하 주차장 이용 가능")
        @Size(max = 1000, message = "기타 정보는 1000자를 초과할 수 없습니다.")
        String extraInfo
) {
}
