package com.ota_service.ota_service.dto.auth;

import com.ota_service.ota_service.security.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "1")
        Long accountId,

        @Schema(example = "guest1@ota.local")
        String email,

        @Schema(example = "이방문")
        String name,

        AccountType accountType,

        @Schema(description = "Bearer prefix 없이 전달되는 JWT access token")
        String accessToken
) {
}
