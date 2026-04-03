package com.ota_service.ota_service.controller.supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.dto.supplier.onda.OndaWebhookAckResponse;
import com.ota_service.ota_service.service.OndaWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Supplier Onda Webhook")
@RestController
@RequestMapping("/api/supplier/onda")
@RequiredArgsConstructor
public class OndaWebhookController {

    private final OndaWebhookService ondaWebhookService;

    @Operation(summary = "Onda supplier webhook 수신", description = "contents/status/inventory 변경 이벤트를 Supplier DB에 동기화합니다.")
    @PutMapping("/webhook")
    public ResponseEntity<ApiResponse<OndaWebhookAckResponse>> receiveWebhook(@RequestBody JsonNode payload) {
        int processedCount = ondaWebhookService.handle(payload);
        return ResponseEntity.ok(ApiResponse.success(
                OndaWebhookAckResponse.of(payload.path("event_type").asText(), processedCount)
        ));
    }
}
