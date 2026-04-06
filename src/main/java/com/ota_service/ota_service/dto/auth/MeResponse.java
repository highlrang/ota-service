package com.ota_service.ota_service.dto.auth;

import lombok.Builder;

@Builder
public record MeResponse(
        String email,
        String code,
        String name
) {
    public static MeResponse of(String email, String code, String name) {
        return MeResponse.builder()
                .email(email)
                .code(code)
                .name(name)
                .build();
    }
}
