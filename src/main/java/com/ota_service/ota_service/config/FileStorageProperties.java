package com.ota_service.ota_service.config;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.upload")
public record FileStorageProperties(
        @Schema(description = "업로드 기본 디렉터리")
        String baseDir
) {
}
