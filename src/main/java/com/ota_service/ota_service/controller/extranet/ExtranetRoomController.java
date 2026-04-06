package com.ota_service.ota_service.controller.extranet;

import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomResponse;
import com.ota_service.ota_service.dto.extranet.room.RoomImageUploadResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomDetailResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomDetailViewResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomImagesRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomImagesResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomInventoriesRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomInventoriesResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomRatesRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomRatesResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomRequest;
import com.ota_service.ota_service.security.AuthenticatedAccount;
import com.ota_service.ota_service.service.ExtranetRoomService;
import com.ota_service.ota_service.service.ExtranetUpdateService;
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

@Tag(name = "Extranet Room")
@RestController
@RequestMapping("/api/extranet")
@RequiredArgsConstructor
public class ExtranetRoomController {

    private final ExtranetRoomService extranetRoomService;
    private final ExtranetUpdateService extranetUpdateService;

    @Operation(summary = "판매자 객실 상세 조회", description = "특정 객실의 상세 정보와 이미지, 요금, 재고를 조회합니다.")
    @GetMapping("/rooms/{roomCode}")
    public ResponseEntity<ApiResponse<ExtranetRoomDetailResponse>> getRoomDetail(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                extranetRoomService.getRoomDetail(account.accountId(), roomCode)
        ));
    }

    @Operation(summary = "객실 이미지 업로드", description = "객실 이미지 파일을 업로드하고 객실 등록 API에 사용할 공개 URL을 반환합니다.")
    @PostMapping(value = "/rooms/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RoomImageUploadResponse>> uploadRoomImage(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(ApiResponse.success(extranetRoomService.uploadRoomImage(account.accountId(), file)));
    }

    @Operation(summary = "판매자 객실 등록", description = "판매자 숙소에 객실 기본정보, 이미지, 재고, 요금을 한 번에 저장합니다.")
    @PostMapping("/accommodations/{accommodationCode}/rooms")
    public ResponseEntity<ApiResponse<CreateExtranetRoomResponse>> createRoom(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String accommodationCode,
            @Valid @RequestBody CreateExtranetRoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                extranetRoomService.createRoom(account.accountId(), accommodationCode, request)
        ));
    }

    @Operation(summary = "객실 정보 수정", description = "객실 기본 정보를 부분 수정합니다.")
    @PatchMapping("/rooms/{roomCode}")
    public ResponseEntity<ApiResponse<ExtranetRoomDetailViewResponse>> updateRoom(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateExtranetRoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                extranetUpdateService.updateRoom(account.accountId(), roomCode, request)
        ));
    }

    @Operation(summary = "객실 이미지 수정", description = "객실 이미지 목록을 전체 교체합니다.")
    @PutMapping("/rooms/{roomCode}/images")
    public ResponseEntity<ApiResponse<UpdateExtranetRoomImagesResponse>> updateRoomImages(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateExtranetRoomImagesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                extranetUpdateService.updateRoomImages(account.accountId(), roomCode, request)
        ));
    }

    @Operation(summary = "객실 요금 수정", description = "객실 요금 override 목록을 전체 교체합니다.")
    @PutMapping("/rooms/{roomCode}/rates")
    public ResponseEntity<ApiResponse<UpdateExtranetRoomRatesResponse>> updateRoomRates(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateExtranetRoomRatesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                extranetUpdateService.updateRoomRates(account.accountId(), roomCode, request)
        ));
    }

    @Operation(summary = "객실 재고 수정", description = "객실 재고 목록을 전체 교체합니다.")
    @PutMapping("/rooms/{roomCode}/inventories")
    public ResponseEntity<ApiResponse<UpdateExtranetRoomInventoriesResponse>> updateRoomInventories(
            @AuthenticationPrincipal AuthenticatedAccount account,
            @PathVariable String roomCode,
            @Valid @RequestBody UpdateExtranetRoomInventoriesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                extranetUpdateService.updateRoomInventories(account.accountId(), roomCode, request)
        ));
    }
}
