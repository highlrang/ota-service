package com.ota_service.ota_service.dto.supplier.onda;

public record OndaWebhookAckResponse(
        String eventType,
        int processedCount
) {

    public static OndaWebhookAckResponse of(String eventType, int processedCount) {
        return new OndaWebhookAckResponse(eventType, processedCount);
    }
}
