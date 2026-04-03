package com.ota_service.ota_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "onda.supplier")
public record OndaSupplierProperties(
        String baseUrl,
        String authorization
) {
}
