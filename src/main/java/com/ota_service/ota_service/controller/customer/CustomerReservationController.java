package com.ota_service.ota_service.controller.customer;

import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationRequest;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationResponse;
import com.ota_service.ota_service.security.AuthenticatedAccount;
import com.ota_service.ota_service.service.CustomerReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customer Reservation")
@RestController
@RequestMapping("/api/customer/reservations")
@RequiredArgsConstructor
public class CustomerReservationController {

    private final CustomerReservationService customerReservationService;

    @Operation(summary = "고객 예약 생성", description = "고객 정보와 숙소/객실/날짜/결제 정보를 받아 예약과 결제를 함께 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateCustomerReservationResponse>> createReservation(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @Valid @RequestBody CreateCustomerReservationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                customerReservationService.createReservation(account == null ? null : account.accountId(), request)
        ));
    }
}
