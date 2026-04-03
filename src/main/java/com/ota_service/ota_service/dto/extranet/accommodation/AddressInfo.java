package com.ota_service.ota_service.dto.extranet.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AddressInfo(
        @Schema(description = "도로명 주소", example = "서울 강남구 테헤란로 100")
        @Size(max = 500, message = "도로명 주소는 500자를 초과할 수 없습니다.")
        String roadAddress,

        @Schema(description = "지번 주소", example = "서울 강남구 역삼동 123-45")
        @NotBlank(message = "지번 주소는 필수입니다.")
        @Size(max = 500, message = "지번 주소는 500자를 초과할 수 없습니다.")
        String address,

        @Schema(description = "위도", example = "37.4980950")
        BigDecimal lat,

        @Schema(description = "경도", example = "127.0276100")
        BigDecimal lng,

        @Schema(description = "1단계 행정구역명", example = "서울")
        @NotBlank(message = "시/도 정보는 필수입니다.")
        @Size(max = 100, message = "시/도 정보는 100자를 초과할 수 없습니다.")
        String region1DepthName,

        @Schema(description = "2단계 행정구역명", example = "강남구")
        @Size(max = 100, message = "시/군/구 정보는 100자를 초과할 수 없습니다.")
        String region2DepthName,

        @Schema(description = "3단계 행정구역명", example = "역삼동")
        @Size(max = 100, message = "읍/면/동 정보는 100자를 초과할 수 없습니다.")
        String region3DepthName
) {
}
