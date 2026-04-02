package com.ota_service.ota_service.controller.seller;

import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.seller.accommodation.CreateSellerAccommodationRequest;
import com.ota_service.ota_service.dto.seller.accommodation.CreateSellerAccommodationResponse;
import com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationDetailResponse;
import com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationSummaryResponse;
import com.ota_service.ota_service.dto.seller.accommodation.UpdateSellerAccommodationRequest;
import com.ota_service.ota_service.security.AuthenticatedAccount;
import com.ota_service.ota_service.service.SellerAccommodationService;
import com.ota_service.ota_service.service.SellerUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Seller Accommodation")
@RestController
@RequestMapping("/api/seller/accommodations")
@RequiredArgsConstructor
public class SellerAccommodationController {

    private final SellerAccommodationService sellerAccommodationService;
    private final SellerUpdateService sellerUpdateService;

    @Operation(summary = "내 등록 숙소 상세 조회", description = "숙소 기본 정보와 객실 요약 목록을 조회합니다.")
    @GetMapping("/{accommodationCode}")
    public ResponseEntity<ApiResponse<SellerAccommodationDetailResponse>> getMyAccommodationDetail(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String accommodationCode
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerAccommodationService.getMyAccommodationDetail(account.accountId(), accommodationCode)
        ));
    }

    @Operation(summary = "내 등록 숙소 목록 조회", description = "Bearer JWT 로 인증된 Extranet 판매자가 등록한 숙소 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerAccommodationSummaryResponse>>> getMyAccommodations(
            @AuthenticationPrincipal AuthenticatedAccount account
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerAccommodationService.getMyAccommodations(account.accountId())
        ));
    }

    @Operation(summary = "판매자 숙소 등록", description = "Extranet 판매자 숙소 원본 DB와 통합 Accommodation DB에 함께 저장합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateSellerAccommodationResponse>> create(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @Valid @RequestBody CreateSellerAccommodationRequest request
    ) {
        CreateSellerAccommodationResponse response = sellerAccommodationService.create(account.accountId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "숙소 정보 수정", description = "숙소 기본 정보를 부분 수정합니다.")
    @org.springframework.web.bind.annotation.PatchMapping("/{accommodationCode}")
    public ResponseEntity<ApiResponse<SellerAccommodationDetailResponse>> updateAccommodation(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String accommodationCode,
            @Valid @RequestBody UpdateSellerAccommodationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerUpdateService.updateAccommodation(account.accountId(), accommodationCode, request)
        ));
    }
}
