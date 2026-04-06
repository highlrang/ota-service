package com.ota_service.ota_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ota_service.ota_service.config.OndaSupplierProperties;
import com.ota_service.ota_service.dto.extranet.accommodation.AddressInfo;
import com.ota_service.ota_service.dto.supplier.onda.OndaPropertyContent;
import com.ota_service.ota_service.dto.supplier.onda.OndaRateplanContent;
import com.ota_service.ota_service.dto.supplier.onda.OndaRoomtypeContent;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.AccommodationType;
import com.ota_service.ota_service.enums.BedType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class OndaApiSupplierContentClient implements OndaSupplierContentClient {

    private final RestClient.Builder restClientBuilder;
    private final OndaSupplierProperties ondaSupplierProperties;
    private final RegionResolveService regionResolveService;

    @Override
    public OndaPropertyContent fetchProperty(String propertyId) {
        JsonNode property = get("/gds/diglett/properties/{propertyId}", propertyId).path("property");
        if (property.isMissingNode() || property.isNull()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda property 응답이 비어 있습니다.");
        }

        JsonNode address = property.path("address");
        JsonNode location = address.path("location");
        String fullAddress = joinAddress(
                address.path("address1").asText(null),
                address.path("address2").asText(null)
        );
        AddressInfo addressInfo = new AddressInfo(
                fullAddress,
                fullAddress,
                decimalOrNull(location.get("latitude")),
                decimalOrNull(location.get("longitude")),
                normalizeRegionName(address.path("region").asText(null)),
                normalizeRegionName(address.path("city").asText(null)),
                null
        );

        return new OndaPropertyContent(
                property.path("id").asText(),
                property.path("name").asText(),
                resolveRegionType(address.path("country_code").asText(null)),
                resolveAccommodationType(property.path("classifications")),
                regionResolveService.resolve(addressInfo).getId(),
                fullAddress,
                decimalOrNull(location.get("latitude")),
                decimalOrNull(location.get("longitude")),
                firstImageUrl(property.path("images")),
                firstNonBlank(
                        property.path("descriptions").path("property").asText(null),
                        property.path("descriptions").path("reservation").asText(null)
                ),
                firstNonBlank(
                        property.path("descriptions").path("notice").asText(null),
                        property.path("descriptions").path("refunds").asText(null)
                ),
                timeOrNull(property.path("checkin").asText(null)),
                timeOrNull(property.path("checkout").asText(null))
        );
    }

    @Override
    public OndaRoomtypeContent fetchRoomtype(String propertyId, String roomtypeId) {
        JsonNode roomtype = get(
                "/gds/diglett/properties/{propertyId}/roomtypes/{roomtypeId}",
                propertyId,
                roomtypeId
        ).path("roomtype");
        if (roomtype.isMissingNode() || roomtype.isNull()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda roomtype 응답이 비어 있습니다.");
        }

        JsonNode capacity = roomtype.path("capacity");
        return new OndaRoomtypeContent(
                roomtype.path("property_id").asText(),
                roomtype.path("id").asText(),
                roomtype.path("name").asText(),
                roomtype.path("description").asText(null),
                intOrNull(capacity.get("standard")),
                intOrNull(capacity.get("max")),
                resolveBedType(roomtype.path("bedtype")),
                buildRoomtypeExtraInfo(roomtype),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public OndaRateplanContent fetchRateplan(String propertyId, String roomtypeId, String rateplanId) {
        JsonNode rateplan = get(
                "/gds/diglett/properties/{propertyId}/roomtypes/{roomtypeId}/rateplans/{rateplanId}",
                propertyId,
                roomtypeId,
                rateplanId
        ).path("rateplan");
        if (rateplan.isMissingNode() || rateplan.isNull()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda rateplan 응답이 비어 있습니다.");
        }

        JsonNode salesTerms = rateplan.path("sales_terms");
        JsonNode lengthOfStay = rateplan.path("length_of_stay");
        return new OndaRateplanContent(
                rateplan.path("property_id").asText(),
                rateplan.path("roomtype_id").asText(),
                rateplan.path("id").asText(),
                rateplan.path("name").asText(),
                null,
                null,
                null,
                boolOrNull(rateplan.get("refundable")),
                dateOrNull(salesTerms.get("from")),
                resolveValidTo(salesTerms.get("to"), lengthOfStay.get("max"))
        );
    }

    private JsonNode get(String uri, Object... uriVariables) {
        if (!StringUtils.hasText(ondaSupplierProperties.baseUrl()) || !StringUtils.hasText(ondaSupplierProperties.authorization())) {
            throw new ApiException(ExceptionType.NOT_IMPLEMENTED, "Onda supplier API 설정이 비어 있습니다.");
        }

        try {
            return restClientBuilder
                    .baseUrl(ondaSupplierProperties.baseUrl())
                    .defaultHeader("Authorization", ondaSupplierProperties.authorization())
                    .build()
                    .get()
                    .uri(uri, uriVariables)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "Onda supplier API 호출에 실패했습니다.");
        }
    }

    private AccommodationRegionType resolveRegionType(String countryCode) {
        return "KR".equalsIgnoreCase(countryCode) ? AccommodationRegionType.DOMESTIC : AccommodationRegionType.OVERSEAS;
    }

    private AccommodationType resolveAccommodationType(JsonNode classifications) {
        String classification = firstArrayText(classifications);
        if (classification == null) {
            return AccommodationType.HOTEL_RESORT;
        }
        if (classification.contains("모텔")) {
            return AccommodationType.MOTEL;
        }
        if (classification.contains("펜션") || classification.contains("풀빌라")) {
            return AccommodationType.PENSION_POOL_VILLA;
        }
        if (classification.contains("캠핑") || classification.contains("글램핑")) {
            return AccommodationType.CAMPING_GLAMPING;
        }
        if (classification.contains("한옥")) {
            return AccommodationType.GUESTHOUSE_HANOK;
        }
        if (classification.contains("게스트하우스") || classification.contains("비앤비") || classification.contains("bnb")) {
            return AccommodationType.GUESTHOUSE_BNB;
        }
        if (classification.contains("호스텔")) {
            return AccommodationType.HOSTEL;
        }
        if (classification.contains("호텔")) {
            return AccommodationType.HOTEL;
        }
        return AccommodationType.HOTEL_RESORT;
    }

    private BedType resolveBedType(JsonNode bedtype) {
        if (bedtype == null || bedtype.isMissingNode() || bedtype.isNull()) {
            return null;
        }

        int single = bedtype.path("single_beds").asInt(0) + bedtype.path("super_single_beds").asInt(0);
        int doubled = bedtype.path("double_beds").asInt(0);
        int queen = bedtype.path("queen_beds").asInt(0);
        int king = bedtype.path("king_beds").asInt(0);
        int sofa = bedtype.path("sofa_beds").asInt(0);
        int air = bedtype.path("air_beds").asInt(0);

        int nonZeroCount = 0;
        if (single > 0) nonZeroCount++;
        if (doubled > 0) nonZeroCount++;
        if (queen > 0) nonZeroCount++;
        if (king > 0) nonZeroCount++;
        if (sofa > 0) nonZeroCount++;
        if (air > 0) nonZeroCount++;

        if (nonZeroCount > 1) {
            return BedType.MIXED;
        }
        if (king > 0) return BedType.KING;
        if (queen > 0) return BedType.QUEEN;
        if (doubled > 0) return BedType.DOUBLE;
        if (single > 1) return BedType.TWIN;
        if (single > 0) return BedType.SINGLE;
        if (sofa > 0 || air > 0) return BedType.MIXED;
        return null;
    }

    private String buildRoomtypeExtraInfo(JsonNode roomtype) {
        StringBuilder builder = new StringBuilder();
        Integer size = intOrNull(roomtype.get("size"));
        if (size != null) {
            builder.append("size=").append(size).append("sqm");
        }
        JsonNode details = roomtype.path("details");
        if (!details.isMissingNode() && !details.isNull()) {
            String detailText = compactObject(details);
            if (StringUtils.hasText(detailText)) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }
                builder.append(detailText);
            }
        }
        return builder.isEmpty() ? null : builder.toString();
    }

    private String compactObject(JsonNode objectNode) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> fieldNames = objectNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode value = objectNode.get(fieldName);
            if (value == null || value.isNull()) {
                continue;
            }
            String text = value.asText();
            if (!StringUtils.hasText(text) || "0".equals(text) || "null".equalsIgnoreCase(text)) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(fieldName).append('=').append(text);
        }
        return builder.toString();
    }

    private String firstImageUrl(JsonNode images) {
        if (images == null || !images.isArray() || images.isEmpty()) {
            return null;
        }
        JsonNode image = images.get(0);
        return firstNonBlank(
                image.path("1000px").asText(null),
                image.path("500px").asText(null),
                image.path("250px").asText(null),
                image.path("original").asText(null)
        );
    }

    private String firstArrayText(JsonNode values) {
        if (values == null || !values.isArray()) {
            return null;
        }
        for (JsonNode value : values) {
            if (value != null && !value.isNull() && StringUtils.hasText(value.asText())) {
                return value.asText();
            }
        }
        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String joinAddress(String address1, String address2) {
        if (StringUtils.hasText(address1) && StringUtils.hasText(address2)) {
            return address1 + " " + address2;
        }
        return firstNonBlank(address1, address2);
    }

    private String normalizeRegionName(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private BigDecimal decimalOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.decimalValue();
    }

    private Integer intOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asInt();
    }

    private Boolean boolOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asBoolean();
    }

    private LocalTime timeOrNull(String value) {
        return StringUtils.hasText(value) ? LocalTime.parse(value) : null;
    }

    private LocalDate dateOrNull(JsonNode node) {
        if (node == null || node.isNull() || !StringUtils.hasText(node.asText())) {
            return null;
        }
        String value = node.asText();
        return LocalDate.parse(value.length() >= 10 ? value.substring(0, 10) : value);
    }

    private LocalDate resolveValidTo(JsonNode salesToNode, JsonNode maxStayNode) {
        LocalDate salesTo = dateOrNull(salesToNode);
        if (salesTo != null) {
            return salesTo;
        }
        Integer maxStay = intOrNull(maxStayNode);
        return maxStay == null || maxStay <= 0 ? null : LocalDate.now().plusDays(maxStay);
    }
}
