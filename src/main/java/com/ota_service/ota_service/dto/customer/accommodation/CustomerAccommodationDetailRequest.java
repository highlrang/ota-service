package com.ota_service.ota_service.dto.customer.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerAccommodationDetailRequest {

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
}
