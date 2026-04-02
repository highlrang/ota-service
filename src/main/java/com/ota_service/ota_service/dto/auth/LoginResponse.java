package com.ota_service.ota_service.dto.auth;

import com.ota_service.ota_service.security.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "guest1@ota.local")
        String email,

        @Schema(example = "USER-0001")
        String code,

        @Schema(example = "이방문")
        String name,

        @Schema(description = "Bearer prefix 없이 전달되는 JWT access token")
        String accessToken,

        @Schema(description = "재발급에 사용하는 refresh token")
        String refreshToken
) {
}
