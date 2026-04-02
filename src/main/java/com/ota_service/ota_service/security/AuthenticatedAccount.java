package com.ota_service.ota_service.security;

public record AuthenticatedAccount(
        Long accountId,
        String email,
        String code,
        String name,
        AccountType accountType
) {
}
