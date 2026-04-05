package com.ota_service.ota_service.dto.auth;

import com.ota_service.ota_service.entity.Extranet;
import com.ota_service.ota_service.entity.User;
import com.ota_service.ota_service.security.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
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
    public static LoginResponse fromCustomer(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .email(user.getEmail())
                .code(user.getCode())
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static LoginResponse fromExtranet(Extranet extranet, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .email(extranet.getEmail())
                .code(extranet.getCode())
                .name(extranet.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
