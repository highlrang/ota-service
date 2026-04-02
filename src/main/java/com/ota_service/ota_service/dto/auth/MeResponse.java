package com.ota_service.ota_service.dto.auth;

import com.ota_service.ota_service.security.AccountType;

public record MeResponse(
        Long accountId,
        String email,
        String name,
        AccountType accountType
) {
}
