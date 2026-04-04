package com.ota_service.ota_service.dto.accommodation.popular;

import com.ota_service.ota_service.enums.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopularAccommodationRequest {

    @Schema(example = "HOTEL_RESORT", description = "조회할 숙소 타입. 미입력 시 전체 타입에서 인기 숙소를 조회합니다.")
    private AccommodationType accommodationType;
}
