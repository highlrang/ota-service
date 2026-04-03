package com.ota_service.ota_service.controller.accommodation;

import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerAccommodationDetailRequest;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerAccommodationDetailResponse;
import com.ota_service.ota_service.dto.accommodation.search.SearchAccommodationPageResponse;
import com.ota_service.ota_service.dto.accommodation.search.SearchAccommodationRequest;
import com.ota_service.ota_service.service.AccommodationSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customer Accommodation")
@RestController
@RequestMapping("/api/customer/accommodations")
@RequiredArgsConstructor
public class AccommodationSearchController {

    private final AccommodationSearchService accommodationSearchService;

    @Operation(summary = "숙소 검색", description = "통합 Accommodation 기준으로 지역, 숙소 타입, 침구 타입, 날짜, 인원, 요금 조건을 반영해 페이징 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SearchAccommodationPageResponse>> search(
            @Valid @ModelAttribute SearchAccommodationRequest request,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(accommodationSearchService.search(request, pageable)));
    }

    @Operation(summary = "숙소 상세 조회", description = "고객이 선택한 숙소의 상세 정보와 예약 단위 객실 목록을 조회합니다.")
    @GetMapping("/{accommodationCode}")
    public ResponseEntity<ApiResponse<CustomerAccommodationDetailResponse>> getAccommodationDetail(
            @PathVariable String accommodationCode,
            @Valid @ModelAttribute CustomerAccommodationDetailRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                accommodationSearchService.getAccommodationDetail(accommodationCode, request)
        ));
    }
}
