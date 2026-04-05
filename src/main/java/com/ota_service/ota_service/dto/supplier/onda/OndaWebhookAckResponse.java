package com.ota_service.ota_service.dto.supplier.onda;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OndaWebhookAckResponse(
        String eventType,
        int processedCount
) {

    public static OndaWebhookAckResponse of(String eventType, int processedCount) {
        return OndaWebhookAckResponse.builder()
                .eventType(eventType)
                .processedCount(processedCount)
                .build();
    }
}
