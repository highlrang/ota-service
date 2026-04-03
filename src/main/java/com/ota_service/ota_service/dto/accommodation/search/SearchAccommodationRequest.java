package com.ota_service.ota_service.dto.accommodation.search;

import com.ota_service.ota_service.enums.AccommodationType;
import com.ota_service.ota_service.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchAccommodationRequest {

    @Schema(example = "KR-11-11680", description = "지역 코드. 현재는 정확히 일치하는 region code 기준으로 조회합니다.")
    private String regionCode;

    @Schema(example = "HOTEL_RESORT")
    private AccommodationType accommodationType;

    @Schema(example = "DOUBLE")
    private BedType bedType;

    @Schema(example = "2026-04-10")
    @NotNull
    @FutureOrPresent
    private LocalDate stayStartDate;

    @Schema(example = "2026-04-12")
    @NotNull
    private LocalDate stayEndDate;

    @Schema(example = "2")
    @NotNull
    @Min(1)
    private Integer guestCount;

    @Schema(example = "200000")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal minTotalAmount;

    @Schema(example = "500000")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal maxTotalAmount;

    @Schema(example = "true", description = "true면 매진 숙소를 결과에서 제외합니다.")
    private Boolean excludeSoldOut;
}
