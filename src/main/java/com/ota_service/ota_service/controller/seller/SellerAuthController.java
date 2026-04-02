package com.ota_service.ota_service.controller.seller;

import com.ota_service.ota_service.dto.auth.LoginRequest;
import com.ota_service.ota_service.dto.auth.LoginResponse;
import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.service.SellerAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Seller Auth")
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerAuthController {

    private final SellerAuthService sellerAuthService;

    @Operation(summary = "판매자 로그인", description = "로그인 성공 시 Swagger Authorize 에 넣을 수 있는 JWT access token 을 반환합니다.")
    @SecurityRequirements
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(sellerAuthService.login(request)));
    }
}
