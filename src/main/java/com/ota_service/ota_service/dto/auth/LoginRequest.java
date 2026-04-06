package com.ota_service.ota_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "guest1@ota.local")
        @NotBlank
        @Email
        String email,

        @Schema(example = "guest1234!")
        @NotBlank
        String password
) {
}
