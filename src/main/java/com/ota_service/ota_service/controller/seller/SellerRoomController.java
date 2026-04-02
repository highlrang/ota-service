package com.ota_service.ota_service.controller.seller;

import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.seller.room.CreateSellerRoomRequest;
import com.ota_service.ota_service.dto.seller.room.CreateSellerRoomResponse;
import com.ota_service.ota_service.dto.seller.room.RoomImageUploadResponse;
import com.ota_service.ota_service.dto.seller.room.SellerRoomDetailResponse;
import com.ota_service.ota_service.dto.seller.room.SellerRoomDetailViewResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomImagesRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomImagesResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomInventoriesRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomInventoriesResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomRatesRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomRatesResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomRequest;
import com.ota_service.ota_service.security.AuthenticatedAccount;
import com.ota_service.ota_service.service.SellerRoomService;
import com.ota_service.ota_service.service.SellerUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Seller Room")
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerRoomController {

    private final SellerRoomService sellerRoomService;
    private final SellerUpdateService sellerUpdateService;

    @Operation(summary = "판매자 객실 상세 조회", description = "특정 객실의 상세 정보와 이미지, 요금, 재고를 조회합니다.")
    @GetMapping("/rooms/{roomCode}")
    public ResponseEntity<ApiResponse<SellerRoomDetailResponse>> getRoomDetail(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerRoomService.getRoomDetail(account.accountId(), roomCode)
        ));
    }

    @Operation(summary = "객실 이미지 업로드", description = "객실 이미지 파일을 업로드하고 객실 등록 API에 사용할 공개 URL을 반환합니다.")
    @PostMapping(value = "/rooms/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RoomImageUploadResponse>> uploadRoomImage(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerRoomService.uploadRoomImage(account.accountId(), file)));
    }

    @Operation(summary = "판매자 객실 등록", description = "판매자 숙소에 객실 기본정보, 이미지, 재고, 요금을 한 번에 저장합니다.")
    @PostMapping("/accommodations/{accommodationCode}/rooms")
    public ResponseEntity<ApiResponse<CreateSellerRoomResponse>> createRoom(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String accommodationCode,
            @Valid @RequestBody CreateSellerRoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerRoomService.createRoom(account.accountId(), accommodationCode, request)
        ));
    }

    @Operation(summary = "객실 정보 수정", description = "객실 기본 정보를 부분 수정합니다.")
    @PatchMapping("/rooms/{roomCode}")
    public ResponseEntity<ApiResponse<SellerRoomDetailViewResponse>> updateRoom(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateSellerRoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerUpdateService.updateRoom(account.accountId(), roomCode, request)
        ));
    }

    @Operation(summary = "객실 이미지 수정", description = "객실 이미지 목록을 전체 교체합니다.")
    @PutMapping("/rooms/{roomCode}/images")
    public ResponseEntity<ApiResponse<UpdateSellerRoomImagesResponse>> updateRoomImages(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateSellerRoomImagesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerUpdateService.updateRoomImages(account.accountId(), roomCode, request)
        ));
    }

    @Operation(summary = "객실 요금 수정", description = "객실 요금 override 목록을 전체 교체합니다.")
    @PutMapping("/rooms/{roomCode}/rates")
    public ResponseEntity<ApiResponse<UpdateSellerRoomRatesResponse>> updateRoomRates(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateSellerRoomRatesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerUpdateService.updateRoomRates(account.accountId(), roomCode, request)
        ));
    }

    @Operation(summary = "객실 재고 수정", description = "객실 재고 목록을 전체 교체합니다.")
    @PutMapping("/rooms/{roomCode}/inventories")
    public ResponseEntity<ApiResponse<UpdateSellerRoomInventoriesResponse>> updateRoomInventories(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateSellerRoomInventoriesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                sellerUpdateService.updateRoomInventories(account.accountId(), roomCode, request)
        ));
    }
}
