package com.ota_service.ota_service.controller.customer;

import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.customer.reservation.CancelCustomerReservationRequest;
import com.ota_service.ota_service.dto.customer.reservation.CancelCustomerReservationResponse;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationRequest;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationResponse;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationDetailResponse;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationListType;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationPageResponse;
import com.ota_service.ota_service.security.AuthenticatedAccount;
import com.ota_service.ota_service.service.CustomerReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(summary = "고객 예약 취소", description = "회원은 로그인 정보로, 비회원은 투숙객명과 휴대폰 번호로 예약을 취소하고 환불 정보를 생성합니다.")
    @PostMapping("/{reservationNo}/cancel")
    public ResponseEntity<ApiResponse<CancelCustomerReservationResponse>> cancelReservation(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String reservationNo,
            @Parameter(description = "비회원 조회 시 필수")
            @RequestParam(required = false) String guestName,
            @Parameter(description = "비회원 조회 시 필수")
            @RequestParam(required = false) String guestPhoneNumber,
            @RequestBody(required = false) CancelCustomerReservationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                customerReservationService.cancelReservation(
                        account == null ? null : account.accountId(),
                        guestName,
                        guestPhoneNumber,
                        reservationNo,
                        request
                )
        ));
    }

    @Operation(summary = "고객 예약 목록 조회", description = "회원은 로그인 정보로, 비회원은 투숙객명과 휴대폰 번호로 국내 숙소 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<CustomerReservationPageResponse>> getReservations(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @RequestParam CustomerReservationListType type,
            @Parameter(description = "비회원 조회 시 필수")
            @RequestParam(required = false) String guestName,
            @Parameter(description = "비회원 조회 시 필수")
            @RequestParam(required = false) String guestPhoneNumber,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                customerReservationService.getReservations(
                        account == null ? null : account.accountId(),
                        guestName,
                        guestPhoneNumber,
                        type,
                        pageable
                )
        ));
    }

    @Operation(summary = "고객 예약 상세 조회", description = "회원은 로그인 정보로, 비회원은 투숙객명과 휴대폰 번호로 국내 숙소 예약 상세와 결제 정보를 조회합니다.")
    @GetMapping("/{reservationNo}")
    public ResponseEntity<ApiResponse<CustomerReservationDetailResponse>> getReservationDetail(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String reservationNo,
            @Parameter(description = "비회원 조회 시 필수")
            @RequestParam(required = false) String guestName,
            @Parameter(description = "비회원 조회 시 필수")
            @RequestParam(required = false) String guestPhoneNumber
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                customerReservationService.getReservationDetail(
                        account == null ? null : account.accountId(),
                        guestName,
                        guestPhoneNumber,
                        reservationNo
                )
        ));
    }
}
