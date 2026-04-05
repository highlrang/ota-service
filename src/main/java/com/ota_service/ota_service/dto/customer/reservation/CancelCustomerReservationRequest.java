package com.ota_service.ota_service.dto.customer.reservation;

import io.swagger.v3.oas.annotations.media.Schema;

public record CancelCustomerReservationRequest(
        @Schema(example = "사용자 요청 취소")
        String reason
) {
}
