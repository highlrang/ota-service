package com.ota_service.ota_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ota_service.ota_service.config.OndaSupplierProperties;
import com.ota_service.ota_service.dto.supplier.onda.OndaCancelReservationRequest;
import com.ota_service.ota_service.dto.supplier.onda.OndaCancelReservationResponse;
import com.ota_service.ota_service.dto.supplier.onda.OndaCheckAvailabilityResponse;
import com.ota_service.ota_service.dto.supplier.onda.OndaCreateReservationRequest;
import com.ota_service.ota_service.dto.supplier.onda.OndaCreateReservationResponse;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class OndaApiSupplierReservationClient implements OndaSupplierReservationClient {

    private final RestClient.Builder restClientBuilder;
    private final OndaSupplierProperties ondaSupplierProperties;

    @Override
    public OndaCheckAvailabilityResponse checkAvailability(
            String propertyId,
            String roomtypeId,
            String rateplanId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        JsonNode result = get(
                "/gds/diglett/properties/{propertyId}/roomtypes/{roomtypeId}/rateplans/{rateplanId}/checkavail",
                propertyId,
                roomtypeId,
                rateplanId,
                checkIn,
                checkOut
        );

        List<OndaCheckAvailabilityResponse.OndaCheckAvailabilityDate> dates = new ArrayList<>();
        JsonNode dateNodes = result.path("dates");
        if (dateNodes.isArray()) {
            for (JsonNode dateNode : dateNodes) {
                dates.add(OndaCheckAvailabilityResponse.OndaCheckAvailabilityDate.of(
                        localDate(dateNode.path("date").asText(null)),
                        dateNode.path("vacancy").isMissingNode() ? null : dateNode.path("vacancy").asInt()
                ));
            }
        }

        return OndaCheckAvailabilityResponse.of(
                localDate(result.path("checkin").asText(null)),
                localDate(result.path("checkout").asText(null)),
                result.path("property_id").asText(),
                result.path("roomtype_id").asText(),
                result.path("rateplan_id").asText(),
                result.path("availability").asBoolean(false),
                dates
        );
    }

    @Override
    public OndaCreateReservationResponse createReservation(String propertyId, OndaCreateReservationRequest request) {
        JsonNode result = post("/gds/diglett/properties/{propertyId}/bookings", request, propertyId);
        return OndaCreateReservationResponse.of(
                result.path("property_id").asText(),
                result.path("property_name").asText(null),
                result.path("booking_number").asText(),
                result.path("channel_booking_number").asText(null),
                result.path("status").asText(null)
        );
    }

    @Override
    public OndaCancelReservationResponse cancelReservation(
            String propertyId,
            String bookingNumber,
            OndaCancelReservationRequest request
    ) {
        JsonNode result = put(
                "/gds/diglett/properties/{propertyId}/bookings/{bookingNumber}/cancel",
                request,
                propertyId,
                bookingNumber
        );
        return OndaCancelReservationResponse.of(
                result.path("booking_number").asText(),
                result.path("channel_booking_number").asText(null),
                result.path("currency").asText(null),
                intOrNull(result.get("total_amount")),
                intOrNull(result.get("refund_amount"))
        );
    }

    private JsonNode get(
            String uri,
            String propertyId,
            String roomtypeId,
            String rateplanId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        ensureConfigured();
        try {
            return baseClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParam("checkin", checkIn)
                            .queryParam("checkout", checkOut)
                            .build(propertyId, roomtypeId, rateplanId))
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda 예약 가능 여부 조회에 실패했습니다.");
        }
    }

    private JsonNode post(String uri, Object body, Object... uriVariables) {
        ensureConfigured();
        try {
            return baseClient()
                    .post()
                    .uri(uri, uriVariables)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("locale", "ko-KR")
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda 예약 생성에 실패했습니다.");
        }
    }

    private JsonNode put(String uri, Object body, Object... uriVariables) {
        ensureConfigured();
        try {
            return baseClient()
                    .put()
                    .uri(uri, uriVariables)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda 예약 취소에 실패했습니다.");
        }
    }

    private RestClient baseClient() {
        return restClientBuilder
                .baseUrl(ondaSupplierProperties.baseUrl())
                .defaultHeader("Authorization", ondaSupplierProperties.authorization())
                .build();
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(ondaSupplierProperties.baseUrl()) || !StringUtils.hasText(ondaSupplierProperties.authorization())) {
            throw new ApiException(ExceptionType.NOT_IMPLEMENTED, "Onda supplier API 설정이 비어 있습니다.");
        }
    }

    private LocalDate localDate(String value) {
        return StringUtils.hasText(value) ? LocalDate.parse(value) : null;
    }

    private Integer intOrNull(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        return node.asInt();
    }
}
