package com.ota_service.ota_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ota_service.ota_service.dto.supplier.onda.OndaInventoryItem;
import com.ota_service.ota_service.dto.supplier.onda.OndaPropertyContent;
import com.ota_service.ota_service.dto.supplier.onda.OndaRateplanContent;
import com.ota_service.ota_service.dto.supplier.onda.OndaRoomtypeContent;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.SupplierAccommodation;
import com.ota_service.ota_service.entity.SupplierRatePlan;
import com.ota_service.ota_service.entity.SupplierRoom;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.entity.SupplierRatePlanInventory;
import com.ota_service.ota_service.enums.AccommodationSourceType;
import com.ota_service.ota_service.enums.BusinessStatus;
import com.ota_service.ota_service.enums.SupplierSourceType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.AccommodationDetailRepository;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.SupplierAccommodationRepository;
import com.ota_service.ota_service.repository.SupplierRatePlanRepository;
import com.ota_service.ota_service.repository.SupplierRoomRepository;
import com.ota_service.ota_service.repository.RoomRateRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.repository.SupplierRatePlanInventoryRepository;
import com.ota_service.ota_service.util.CodeGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OndaWebhookService {

    private static final SupplierSourceType SOURCE = SupplierSourceType.ONDA;

    private final AccommodationRepository accommodationRepository;
    private final AccommodationDetailRepository accommodationDetailRepository;
    private final SupplierAccommodationRepository supplierAccommodationRepository;
    private final SupplierRoomRepository supplierRoomRepository;
    private final SupplierRatePlanRepository supplierRatePlanRepository;
    private final RoomRepository roomRepository;
    private final RoomRateRepository roomRateRepository;
    private final SupplierRatePlanInventoryRepository supplierRatePlanInventoryRepository;
    private final OndaSupplierContentClient ondaSupplierContentClient;
    private final CodeGenerator codeGenerator;

    @Transactional
    public int handle(JsonNode payload) {
        String eventType = requiredText(payload, "event_type");
        return switch (eventType) {
            case "contents_updated" -> {
                handleContentsUpdated(payload.path("event_detail"));
                yield 1;
            }
            case "status_updated" -> {
                handleStatusUpdated(payload.path("event_detail"));
                yield 1;
            }
            case "inventory_updated" -> handleInventoryUpdated(payload.path("event_details"));
            default -> throw new ApiException(ExceptionType.INVALID_INPUT, "지원하지 않는 Onda event_type 입니다: " + eventType);
        };
    }

    private void handleContentsUpdated(JsonNode eventDetail) {
        String target = requiredText(eventDetail, "target");
        switch (target) {
            case "property" -> upsertProperty(requiredId(eventDetail, "property_id"));
            case "roomtype" -> upsertRoomtype(
                    requiredId(eventDetail, "property_id"),
                    requiredId(eventDetail, "roomtype_id")
            );
            case "rateplan" -> upsertRateplan(
                    requiredId(eventDetail, "property_id"),
                    requiredId(eventDetail, "roomtype_id"),
                    requiredId(eventDetail, "rateplan_id")
            );
            default -> throw new ApiException(ExceptionType.INVALID_INPUT, "지원하지 않는 contents target 입니다: " + target);
        }
    }

    private void handleStatusUpdated(JsonNode eventDetail) {
        String target = requiredText(eventDetail, "target");
        String status = requiredText(eventDetail, "status");

        switch (target) {
            case "property" -> updatePropertyStatus(requiredId(eventDetail, "property_id"), status);
            case "roomtype" -> updateRoomtypeStatus(
                    requiredId(eventDetail, "property_id"),
                    requiredId(eventDetail, "roomtype_id"),
                    status
            );
            case "rateplan" -> updateRateplanStatus(
                    requiredId(eventDetail, "property_id"),
                    requiredId(eventDetail, "roomtype_id"),
                    requiredId(eventDetail, "rateplan_id"),
                    status
            );
            default -> throw new ApiException(ExceptionType.INVALID_INPUT, "지원하지 않는 status target 입니다: " + target);
        }
    }

    private int handleInventoryUpdated(JsonNode eventDetails) {
        if (!eventDetails.isArray()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "inventory_updated 의 event_details 는 배열이어야 합니다.");
        }

        int processedCount = 0;
        for (JsonNode itemNode : eventDetails) {
            OndaInventoryItem item = toInventoryItem(itemNode);
            upsertInventory(item);
            processedCount++;
        }
        return processedCount;
    }

    private SupplierAccommodation upsertProperty(String propertyId) {
        OndaPropertyContent content = ondaSupplierContentClient.fetchProperty(propertyId);

        SupplierAccommodation supplierAccommodation = supplierAccommodationRepository
                .findBySourceAndSupplierPropertyId(SOURCE, propertyId)
                .orElse(null);

        Accommodation accommodation;
        boolean isNew = false;

        if (supplierAccommodation != null) {
            accommodation = accommodationRepository.findById(supplierAccommodation.getAccommodationId())
                    .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "외부 숙소 매핑에 연결된 통합 숙소가 없습니다."));
            accommodation.updateSupplier(
                    content.name(),
                    content.regionType(),
                    content.accommodationType(),
                    content.regionId(),
                    content.address(),
                    content.latitude(),
                    content.longitude(),
                    content.thumbnailImage(),
                    defaultTime(content.checkInTime(), LocalTime.of(15, 0)),
                    defaultTime(content.checkOutTime(), LocalTime.of(11, 0))
            );
            accommodation.setBusinessStatus(BusinessStatus.OPEN);
            if (accommodation.getSourceType() == AccommodationSourceType.SUPPLIER) {
                accommodation.setSupplierProductId(propertyId);
            }
        } else {
            accommodation = accommodationRepository.findFirstByNameAndAddress(content.name(), content.address())
                    .orElseGet(() -> {
                        Accommodation newAccommodation = Accommodation.createSupplier(
                                content.name(),
                                content.regionType(),
                                content.accommodationType(),
                                content.regionId(),
                                content.address(),
                                content.latitude(),
                                content.longitude(),
                                content.thumbnailImage(),
                                defaultTime(content.checkInTime(), LocalTime.of(15, 0)),
                                defaultTime(content.checkOutTime(), LocalTime.of(11, 0)),
                                codeGenerator.generateTemporaryAccommodationCode()
                        );
                        newAccommodation.setSupplierProductId(propertyId);
                        return newAccommodation;
                    });

            if (accommodation.getId() == null) {
                isNew = true;
            }

            if (accommodation.getSourceType() == AccommodationSourceType.SUPPLIER) {
                accommodation.updateSupplier(
                        content.name(),
                        content.regionType(),
                        content.accommodationType(),
                        content.regionId(),
                        content.address(),
                        content.latitude(),
                        content.longitude(),
                        content.thumbnailImage(),
                        defaultTime(content.checkInTime(), LocalTime.of(15, 0)),
                        defaultTime(content.checkOutTime(), LocalTime.of(11, 0))
                );
                accommodation.setBusinessStatus(BusinessStatus.OPEN);
                accommodation.setSupplierProductId(propertyId);
            }
        }

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        if (isNew) {
            savedAccommodation.setCode(codeGenerator.generateAccommodationCode(savedAccommodation.getId()));
            savedAccommodation = accommodationRepository.save(savedAccommodation);
        }

        if (savedAccommodation.getSourceType() == AccommodationSourceType.SUPPLIER) {
            Long savedAccommodationId = savedAccommodation.getId();
            AccommodationDetail detail = accommodationDetailRepository.findByAccommodationId(savedAccommodation.getId())
                    .orElseGet(() -> AccommodationDetail.of(savedAccommodationId, content.description(), content.extraInfo()));
            detail.update(content.description(), content.extraInfo());
            accommodationDetailRepository.save(detail);
        }

        SupplierAccommodation savedSupplierAccommodation = supplierAccommodation == null
                ? SupplierAccommodation.of(SOURCE, propertyId, savedAccommodation.getId())
                : supplierAccommodation;
        savedSupplierAccommodation.setAccommodationId(savedAccommodation.getId());
        savedSupplierAccommodation.markSynced("enabled");
        return supplierAccommodationRepository.save(savedSupplierAccommodation);
    }

    private SupplierRoom upsertRoomtype(String propertyId, String roomtypeId) {
        SupplierAccommodation supplierAccommodation = ensureSupplierAccommodation(propertyId);
        Accommodation accommodation = accommodationRepository.findById(supplierAccommodation.getAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "외부 숙소 매핑에 연결된 통합 숙소가 없습니다."));
        OndaRoomtypeContent content = ondaSupplierContentClient.fetchRoomtype(propertyId, roomtypeId);

        SupplierRoom supplierRoom = supplierRoomRepository.findBySourceAndSupplierRoomtypeId(SOURCE, roomtypeId).orElse(null);
        Room room = supplierRoom == null
                ? Room.create(
                        accommodation.getId(),
                        codeGenerator.generateTemporaryRoomCode(),
                        content.name(),
                        content.description(),
                        defaultInteger(content.standardOccupancy(), 2),
                        defaultInteger(content.maxOccupancy(), defaultInteger(content.standardOccupancy(), 2)),
                        content.bedType(),
                        content.extraInfo(),
                        defaultPrice(content.basePrice()),
                        defaultCurrency(content.currency()),
                        defaultSalePrice(content.basePrice(), content.salePrice()),
                        defaultBoolean(content.refundable(), false),
                        defaultInteger(content.minStayNights(), 1),
                        defaultInteger(content.maxStayNights(), 30),
                        defaultInteger(content.defaultStock(), 0)
                )
                : roomRepository.findById(supplierRoom.getRoomId())
                        .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "외부 객실 매핑에 연결된 통합 객실이 없습니다."));

        room.setAccommodationId(accommodation.getId());
        room.update(
                content.name(),
                content.description(),
                defaultInteger(content.standardOccupancy(), 2),
                defaultInteger(content.maxOccupancy(), defaultInteger(content.standardOccupancy(), 2)),
                content.bedType(),
                content.extraInfo(),
                defaultPrice(content.basePrice()),
                defaultCurrency(content.currency()),
                defaultSalePrice(content.basePrice(), content.salePrice()),
                defaultBoolean(content.refundable(), false),
                defaultInteger(content.minStayNights(), 1),
                defaultInteger(content.maxStayNights(), 30),
                defaultInteger(content.defaultStock(), 0)
        );
        room.activate();

        boolean isNew = room.getId() == null;
        Room savedRoom = roomRepository.save(room);
        if (isNew) {
            savedRoom.changeRoomCode(codeGenerator.generateRoomCode(savedRoom.getId()));
            savedRoom = roomRepository.save(savedRoom);
        }

        SupplierRoom savedSupplierRoom = supplierRoom == null
                ? SupplierRoom.of(SOURCE, roomtypeId, savedRoom.getId(), supplierAccommodation.getId())
                : supplierRoom;
        savedSupplierRoom.setRoomId(savedRoom.getId());
        savedSupplierRoom.setSupplierAccommodationId(supplierAccommodation.getId());
        savedSupplierRoom.markSynced("enabled");
        return supplierRoomRepository.save(savedSupplierRoom);
    }

    private SupplierRatePlan upsertRateplan(String propertyId, String roomtypeId, String rateplanId) {
        SupplierRoom supplierRoom = ensureSupplierRoom(propertyId, roomtypeId);
        Room room = roomRepository.findById(supplierRoom.getRoomId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "외부 객실 매핑에 연결된 통합 객실이 없습니다."));
        OndaRateplanContent content = ondaSupplierContentClient.fetchRateplan(propertyId, roomtypeId, rateplanId);

        LocalDate validFrom = content.validFrom() != null ? content.validFrom() : LocalDate.now();
        LocalDate validTo = content.validTo() != null ? content.validTo() : validFrom.plusYears(10);

        SupplierRatePlan supplierRatePlan = supplierRatePlanRepository
                .findBySourceAndSupplierRateplanId(SOURCE, rateplanId)
                .orElse(null);
        RoomRate roomRate = supplierRatePlan == null
                ? RoomRate.create(
                        room.getId(),
                        defaultRateName(content.name(), rateplanId),
                        defaultPrice(content.basePrice()),
                        defaultCurrency(content.currency()),
                        defaultSalePrice(content.basePrice(), content.salePrice()),
                        defaultBoolean(content.refundable(), false),
                        validFrom,
                        validTo
                )
                : roomRateRepository.findById(supplierRatePlan.getRoomRateId())
                        .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "외부 요금제 매핑에 연결된 통합 요금제가 없습니다."));

        roomRate.setRoomId(room.getId());
        roomRate.update(
                defaultRateName(content.name(), rateplanId),
                defaultPrice(content.basePrice()),
                defaultCurrency(content.currency()),
                defaultSalePrice(content.basePrice(), content.salePrice()),
                defaultBoolean(content.refundable(), false),
                validFrom,
                validTo
        );

        RoomRate savedRoomRate = roomRateRepository.save(roomRate);
        SupplierRatePlan savedSupplierRatePlan = supplierRatePlan == null
                ? SupplierRatePlan.of(SOURCE, rateplanId, savedRoomRate.getId(), supplierRoom.getId())
                : supplierRatePlan;
        savedSupplierRatePlan.setRoomRateId(savedRoomRate.getId());
        savedSupplierRatePlan.setSupplierRoomId(supplierRoom.getId());
        savedSupplierRatePlan.updateContent(
                defaultRateName(content.name(), rateplanId),
                defaultPrice(content.basePrice()),
                defaultCurrency(content.currency()),
                defaultSalePrice(content.basePrice(), content.salePrice()),
                defaultBoolean(content.refundable(), false),
                validFrom,
                validTo
        );
        savedSupplierRatePlan.markSynced("enabled");
        return supplierRatePlanRepository.save(savedSupplierRatePlan);
    }

    private void updatePropertyStatus(String propertyId, String status) {
        if ("enabled".equals(status)) {
            upsertProperty(propertyId);
            return;
        }

        supplierAccommodationRepository.findBySourceAndSupplierPropertyId(SOURCE, propertyId)
                .ifPresent(supplierAccommodation -> {
                    supplierAccommodation.markSynced(status);
                    supplierAccommodationRepository.save(supplierAccommodation);

                    accommodationRepository.findById(supplierAccommodation.getAccommodationId())
                            .ifPresent(accommodation -> {
                                accommodation.setBusinessStatus(BusinessStatus.CLOSED);
                                accommodationRepository.save(accommodation);
                            });
                });
    }

    private void updateRoomtypeStatus(String propertyId, String roomtypeId, String status) {
        if ("enabled".equals(status)) {
            upsertRoomtype(propertyId, roomtypeId);
            return;
        }

        supplierRoomRepository.findBySourceAndSupplierRoomtypeId(SOURCE, roomtypeId)
                .ifPresent(supplierRoom -> {
                    supplierRoom.markSynced(status);
                    supplierRoomRepository.save(supplierRoom);

                    roomRepository.findById(supplierRoom.getRoomId())
                            .ifPresent(room -> {
                                room.deactivate(resolveDeletedAt(status));
                                roomRepository.save(room);
                            });
                });
    }

    private void updateRateplanStatus(String propertyId, String roomtypeId, String rateplanId, String status) {
        if ("enabled".equals(status)) {
            upsertRateplan(propertyId, roomtypeId, rateplanId);
            return;
        }

        supplierRatePlanRepository.findBySourceAndSupplierRateplanId(SOURCE, rateplanId)
                .ifPresent(supplierRatePlan -> {
                    supplierRatePlan.markSynced(status);
                    supplierRatePlanRepository.save(supplierRatePlan);

                    roomRateRepository.findById(supplierRatePlan.getRoomRateId())
                            .ifPresent(roomRate -> {
                                roomRate.softDelete(resolveDeletedAt(status));
                                roomRateRepository.save(roomRate);
                            });
                });
    }

    private void upsertInventory(OndaInventoryItem item) {
        if (item.salePrice().compareTo(item.basicPrice()) > 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "sale_price 는 basic_price 보다 클 수 없습니다.");
        }

        SupplierRatePlan supplierRatePlan = ensureSupplierRatePlan(item.propertyId(), item.roomtypeId(), item.rateplanId());
        RoomRate roomRate = roomRateRepository.findById(supplierRatePlan.getRoomRateId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "외부 요금제 매핑에 연결된 통합 요금제가 없습니다."));

        SupplierRatePlanInventory inventory = supplierRatePlanInventoryRepository.findByRoomRateIdAndInventoryDate(
                roomRate.getId(),
                item.date()
        ).orElseGet(() -> SupplierRatePlanInventory.create(
                roomRate.getId(),
                item.date(),
                item.basicPrice(),
                item.salePrice(),
                defaultPrice(item.extraAdult()),
                defaultPrice(item.extraChild()),
                defaultPrice(item.extraInfant()),
                item.promotionType(),
                defaultInteger(item.vacancy(), 0)
        ));

        inventory.update(
                item.basicPrice(),
                item.salePrice(),
                defaultPrice(item.extraAdult()),
                defaultPrice(item.extraChild()),
                defaultPrice(item.extraInfant()),
                item.promotionType(),
                defaultInteger(item.vacancy(), 0)
        );
        supplierRatePlanInventoryRepository.save(inventory);
        supplierRatePlan.markSynced("enabled");
        supplierRatePlanRepository.save(supplierRatePlan);
    }

    private SupplierAccommodation ensureSupplierAccommodation(String propertyId) {
        return supplierAccommodationRepository.findBySourceAndSupplierPropertyId(SOURCE, propertyId)
                .orElseGet(() -> upsertProperty(propertyId));
    }

    private SupplierRoom ensureSupplierRoom(String propertyId, String roomtypeId) {
        return supplierRoomRepository.findBySourceAndSupplierRoomtypeId(SOURCE, roomtypeId)
                .orElseGet(() -> upsertRoomtype(propertyId, roomtypeId));
    }

    private SupplierRatePlan ensureSupplierRatePlan(String propertyId, String roomtypeId, String rateplanId) {
        return supplierRatePlanRepository.findBySourceAndSupplierRateplanId(SOURCE, rateplanId)
                .orElseGet(() -> upsertRateplan(propertyId, roomtypeId, rateplanId));
    }

    private OndaInventoryItem toInventoryItem(JsonNode itemNode) {
        return new OndaInventoryItem(
                requiredId(itemNode, "property_id"),
                requiredId(itemNode, "roomtype_id"),
                requiredId(itemNode, "rateplan_id"),
                LocalDate.parse(requiredText(itemNode, "date")),
                requiredDecimal(itemNode, "basic_price"),
                requiredDecimal(itemNode, "sale_price"),
                decimalOrZero(itemNode, "extra_adult"),
                decimalOrZero(itemNode, "extra_child"),
                decimalOrZero(itemNode, "extra_infant"),
                nullableText(itemNode, "promotion_type"),
                requiredInt(itemNode, "vacancy")
        );
    }

    private String requiredId(JsonNode node, String fieldName) {
        return requiredText(node, fieldName);
    }

    private String requiredText(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull() || value.asText().isBlank()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, fieldName + " 는 필수입니다.");
        }
        return value.asText();
    }

    private String nullableText(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text.isBlank() ? null : text;
    }

    private BigDecimal requiredDecimal(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, fieldName + " 는 필수입니다.");
        }
        return value.decimalValue();
    }

    private BigDecimal decimalOrZero(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        return value == null || value.isNull() ? BigDecimal.ZERO : value.decimalValue();
    }

    private int requiredInt(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, fieldName + " 는 필수입니다.");
        }
        return value.asInt();
    }

    private BigDecimal defaultPrice(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal defaultSalePrice(BigDecimal basePrice, BigDecimal salePrice) {
        BigDecimal normalizedBasePrice = defaultPrice(basePrice);
        if (salePrice == null) {
            return normalizedBasePrice;
        }
        if (salePrice.compareTo(normalizedBasePrice) > 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "sale_price 는 basic_price 보다 클 수 없습니다.");
        }
        return salePrice;
    }

    private String defaultCurrency(String currency) {
        return currency != null && !currency.isBlank() ? currency : "KRW";
    }

    private Boolean defaultBoolean(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    private Integer defaultInteger(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    private String defaultRateName(String value, String rateplanId) {
        return value != null && !value.isBlank() ? value : "ONDA-" + rateplanId;
    }

    private LocalTime defaultTime(LocalTime value, LocalTime defaultValue) {
        return value != null ? value : defaultValue;
    }

    private LocalDateTime resolveDeletedAt(String status) {
        if (!"deleted".equals(status) && !"disabled".equals(status)) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "지원하지 않는 status 입니다: " + status);
        }
        return LocalDateTime.from(OffsetDateTime.now());
    }
}
