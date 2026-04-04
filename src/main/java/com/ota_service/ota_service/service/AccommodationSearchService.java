package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.accommodation.popular.PopularAccommodationRequest;
import com.ota_service.ota_service.dto.accommodation.popular.PopularAccommodationResponse;
import com.ota_service.ota_service.dto.accommodation.search.SearchAccommodationItemResponse;
import com.ota_service.ota_service.dto.accommodation.search.SearchAccommodationPageResponse;
import com.ota_service.ota_service.dto.accommodation.search.SearchAccommodationRequest;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerAccommodationDetailRequest;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerAccommodationDetailResponse;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerAccommodationImageResponse;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerAccommodationRoomResponse;
import com.ota_service.ota_service.dto.customer.accommodation.CustomerRoomImageResponse;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomImage;
import com.ota_service.ota_service.entity.RoomInventory;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.enums.BusinessStatus;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.AccommodationDetailRepository;
import com.ota_service.ota_service.repository.AccommodationImageRepository;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.RegionRepository;
import com.ota_service.ota_service.repository.RoomImageRepository;
import com.ota_service.ota_service.repository.RoomInventoryRepository;
import com.ota_service.ota_service.repository.RoomRateRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "cacheManager")
public class AccommodationSearchService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationDetailRepository accommodationDetailRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final RegionRepository regionRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomRateRepository roomRateRepository;
    private final RoomInventoryRepository roomInventoryRepository;

    @Transactional(readOnly = true)
    public SearchAccommodationPageResponse search(SearchAccommodationRequest request, Pageable pageable) {
        validateRequest(request);

        Long regionId = resolveRegionId(request.getRegionCode());
        LocalDate lastStayDate = request.getStayEndDate().minusDays(1);
        long stayNights = ChronoUnit.DAYS.between(request.getStayStartDate(), request.getStayEndDate());
        boolean excludeSoldOut = Boolean.TRUE.equals(request.getExcludeSoldOut());

        Pageable normalizedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Accommodation> page = accommodationRepository.searchAvailableAccommodations(
                regionId,
                request.getAccommodationType() == null ? null : request.getAccommodationType().name(),
                request.getBedType() == null ? null : request.getBedType().name(),
                request.getStayStartDate(),
                lastStayDate,
                request.getGuestCount(),
                request.getMinTotalAmount(),
                request.getMaxTotalAmount(),
                stayNights,
                excludeSoldOut,
                normalizedPageable
        );

        List<SearchAccommodationItemResponse> content = buildContent(
                page.getContent(),
                request,
                lastStayDate,
                stayNights,
                excludeSoldOut
        );
        return new SearchAccommodationPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public CustomerAccommodationDetailResponse getAccommodationDetail(
            String accommodationCode,
            CustomerAccommodationDetailRequest request
    ) {
        validateStayPeriod(request.getStayStartDate(), request.getStayEndDate());

        Accommodation accommodation = accommodationRepository.findByCode(accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 숙소 코드입니다."));
        validateAccommodation(accommodation);

        AccommodationDetail detail = accommodationDetailRepository.findByAccommodationId(accommodation.getId()).orElse(null);
        Region region = regionRepository.findById(accommodation.getRegionId()).orElse(null);
        List<CustomerAccommodationImageResponse> images = accommodationImageRepository
                .findAllByAccommodationIdOrderBySortOrderAsc(accommodation.getId())
                .stream()
                .map(CustomerAccommodationImageResponse::from)
                .toList();

        LocalDate lastStayDate = request.getStayEndDate().minusDays(1);
        long stayNights = ChronoUnit.DAYS.between(request.getStayStartDate(), request.getStayEndDate());
        List<Room> rooms = roomRepository.findAllByAccommodationIdOrderByIdAsc(accommodation.getId()).stream()
                .filter(room -> Boolean.TRUE.equals(room.getActive()))
                .filter(room -> isEligibleRoom(room, request.getGuestCount(), stayNights))
                .toList();

        List<Long> roomIds = rooms.stream().map(Room::getId).toList();
        Map<Long, List<RoomRate>> rateMap = roomIds.isEmpty()
                ? Map.of()
                : roomRateRepository.findAllByRoomIdInAndActiveTrueAndRateDateBetweenOrderByRoomIdAscRateDateAsc(
                        roomIds,
                        request.getStayStartDate(),
                        lastStayDate
                ).stream()
                .collect(java.util.stream.Collectors.groupingBy(RoomRate::getRoomId, LinkedHashMap::new, java.util.stream.Collectors.toList()));
        Map<Long, List<RoomInventory>> inventoryMap = roomIds.isEmpty()
                ? Map.of()
                : roomInventoryRepository.findAllByRoomIdInAndInventoryDateBetweenAndActiveTrueOrderByRoomIdAscInventoryDateAsc(
                        roomIds,
                        request.getStayStartDate(),
                        lastStayDate
                ).stream()
                .collect(java.util.stream.Collectors.groupingBy(RoomInventory::getRoomId, LinkedHashMap::new, java.util.stream.Collectors.toList()));
        Map<Long, List<CustomerRoomImageResponse>> roomImageMap = roomIds.isEmpty()
                ? Map.of()
                : roomImageRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscSortOrderAsc(roomIds).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        RoomImage::getRoomId,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.mapping(CustomerRoomImageResponse::from, java.util.stream.Collectors.toList())
                ));

        List<CustomerAccommodationRoomResponse> roomResponses = rooms.stream()
                .map(room -> buildCustomerRoomResponse(
                        room,
                        rateMap.getOrDefault(room.getId(), List.of()),
                        inventoryMap.getOrDefault(room.getId(), List.of()),
                        roomImageMap.getOrDefault(room.getId(), List.of()),
                        stayNights
                ))
                .flatMap(Optional::stream)
                .toList();

        return CustomerAccommodationDetailResponse.of(
                accommodation,
                detail,
                region,
                request.getStayStartDate(),
                request.getStayEndDate(),
                request.getGuestCount(),
                images,
                roomResponses
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = AccommodationCacheNames.POPULAR,
            key = "#request.accommodationType == null ? 'ALL' : #request.accommodationType.name()"
    )
    public List<PopularAccommodationResponse> getPopularAccommodations(PopularAccommodationRequest request) {
        LocalDate baseDate = LocalDate.now();

        List<Accommodation> accommodations = accommodationRepository.findPopularAccommodations(
                request.getAccommodationType() == null ? null : request.getAccommodationType().name(),
                baseDate
        );
        if (accommodations.isEmpty()) {
            return List.of();
        }

        Map<Long, Region> regionMap = new LinkedHashMap<>();
        regionRepository.findAllById(accommodations.stream().map(Accommodation::getRegionId).distinct().toList())
                .forEach(region -> regionMap.put(region.getId(), region));

        List<Long> accommodationIds = accommodations.stream().map(Accommodation::getId).toList();
        List<Room> rooms = roomRepository.findAllByAccommodationIdInAndActiveTrueOrderByAccommodationIdAscIdAsc(accommodationIds);
        Map<Long, List<Room>> roomMap = rooms.stream()
                .collect(java.util.stream.Collectors.groupingBy(Room::getAccommodationId, LinkedHashMap::new, java.util.stream.Collectors.toList()));

        List<Long> roomIds = rooms.stream().map(Room::getId).toList();
        Map<Long, List<RoomRate>> roomRateMap = roomIds.isEmpty()
                ? Map.of()
                : roomRateRepository.findAllByRoomIdInAndActiveTrueAndRateDateGreaterThanEqualOrderByRoomIdAscRateDateAsc(
                        roomIds,
                        baseDate
                ).stream()
                .collect(java.util.stream.Collectors.groupingBy(RoomRate::getRoomId, LinkedHashMap::new, java.util.stream.Collectors.toList()));

        List<PopularAccommodationResponse> responses = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            DisplayPriceSummary displayPrice = resolveDisplayPrice(
                    roomMap.getOrDefault(accommodation.getId(), List.of()),
                    roomRateMap
            );
            if (displayPrice == null) {
                continue;
            }

            Region region = regionMap.get(accommodation.getRegionId());
            responses.add(new PopularAccommodationResponse(
                    accommodation.getCode(),
                    accommodation.getName(),
                    accommodation.getAccommodationType().name(),
                    region == null ? null : region.getCode(),
                    region == null ? null : region.getFullName(),
                    accommodation.getAddress(),
                    accommodation.getThumbnailImage(),
                    displayPrice.amount(),
                    displayPrice.currency()
            ));
        }
        return responses;
    }

    private void validateRequest(SearchAccommodationRequest request) {
        validateStayPeriod(request.getStayStartDate(), request.getStayEndDate());
        if (request.getMinTotalAmount() != null && request.getMaxTotalAmount() != null
                && request.getMinTotalAmount().compareTo(request.getMaxTotalAmount()) > 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "최소 요금은 최대 요금보다 클 수 없습니다.");
        }
    }

    private void validateStayPeriod(LocalDate stayStartDate, LocalDate stayEndDate) {
        if (!stayEndDate.isAfter(stayStartDate)) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "숙박 종료일은 숙박 시작일보다 이후여야 합니다.");
        }
    }

    private void validateAccommodation(Accommodation accommodation) {
        if (accommodation.getBusinessStatus() != BusinessStatus.OPEN) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "예약 가능한 상태의 숙소가 아닙니다.");
        }
    }

    private Long resolveRegionId(String regionCode) {
        if (regionCode == null || regionCode.isBlank()) {
            return null;
        }
        return regionRepository.findByCode(regionCode)
                .map(Region::getId)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 지역 코드입니다."));
    }

    private DisplayPriceSummary resolveDisplayPrice(
            List<Room> rooms,
            Map<Long, List<RoomRate>> roomRateMap
    ) {
        RoomRate minRate = rooms.stream()
                .flatMap(room -> roomRateMap.getOrDefault(room.getId(), List.of()).stream())
                .min(Comparator.comparing(RoomRate::getSalePrice))
                .orElse(null);
        if (minRate == null) {
            return null;
        }
        return new DisplayPriceSummary(minRate.getSalePrice(), minRate.getCurrency());
    }

    private List<SearchAccommodationItemResponse> buildContent(
            List<Accommodation> accommodations,
            SearchAccommodationRequest request,
            LocalDate lastStayDate,
            long stayNights,
            boolean excludeSoldOut
    ) {
        if (accommodations.isEmpty()) {
            return List.of();
        }

        List<Long> accommodationIds = accommodations.stream()
                .map(Accommodation::getId)
                .toList();
        Map<Long, Region> regionMap = new LinkedHashMap<>();
        regionRepository.findAllById(accommodations.stream().map(Accommodation::getRegionId).distinct().toList())
                .forEach(region -> regionMap.put(region.getId(), region));

        List<Room> rooms = roomRepository.findAllByAccommodationIdInAndActiveTrueOrderByAccommodationIdAscIdAsc(accommodationIds);
        List<Long> roomIds = rooms.stream().map(Room::getId).toList();
        Map<Long, List<RoomRate>> rateMap = roomIds.isEmpty()
                ? Map.of()
                : roomRateRepository.findAllByRoomIdInAndActiveTrueAndRateDateBetweenOrderByRoomIdAscRateDateAsc(
                        roomIds,
                        request.getStayStartDate(),
                        lastStayDate
                ).stream()
                .collect(java.util.stream.Collectors.groupingBy(RoomRate::getRoomId, LinkedHashMap::new, java.util.stream.Collectors.toList()));
        Map<Long, List<RoomInventory>> inventoryMap = roomIds.isEmpty()
                ? Map.of()
                : roomInventoryRepository.findAllByRoomIdInAndInventoryDateBetweenAndActiveTrueOrderByRoomIdAscInventoryDateAsc(
                        roomIds,
                        request.getStayStartDate(),
                        lastStayDate
                ).stream().collect(java.util.stream.Collectors.groupingBy(
                        RoomInventory::getRoomId,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        Map<Long, List<Room>> roomMap = rooms.stream()
                .collect(java.util.stream.Collectors.groupingBy(Room::getAccommodationId, LinkedHashMap::new, java.util.stream.Collectors.toList()));

        List<SearchAccommodationItemResponse> responses = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            List<Room> candidateRooms = roomMap.getOrDefault(accommodation.getId(), List.of()).stream()
                    .filter(room -> isMatchingRoom(room, request, stayNights))
                    .toList();

            List<RoomMatch> matches = candidateRooms.stream()
                    .map(room -> buildRoomMatch(room, rateMap.getOrDefault(room.getId(), List.of()), inventoryMap.getOrDefault(room.getId(), List.of()),
                            request.getStayStartDate(), lastStayDate, stayNights, request.getMinTotalAmount(), request.getMaxTotalAmount(), excludeSoldOut))
                    .flatMap(Optional::stream)
                    .toList();

            if (matches.isEmpty()) {
                continue;
            }

            RoomMatch cheapest = matches.stream()
                    .min(Comparator.comparing(RoomMatch::totalAmount))
                    .orElseThrow();
            Region region = regionMap.get(accommodation.getRegionId());

            responses.add(new SearchAccommodationItemResponse(
                    accommodation.getCode(),
                    accommodation.getName(),
                    accommodation.getAccommodationType().name(),
                    region == null ? null : region.getCode(),
                    region == null ? null : region.getFullName(),
                    accommodation.getAddress(),
                    accommodation.getThumbnailImage(),
                    accommodation.getCheckInTime(),
                    accommodation.getCheckOutTime(),
                    request.getStayStartDate(),
                    request.getStayEndDate(),
                    cheapest.totalAmount(),
                    cheapest.currency(),
                    matches.stream().filter(match -> !match.soldOut()).toList().size(),
                    matches.stream().map(RoomMatch::maxOccupancy).max(Integer::compareTo).orElse(request.getGuestCount()),
                    matches.stream().allMatch(RoomMatch::soldOut)
            ));
        }
        return responses;
    }

    private boolean isMatchingRoom(Room room, SearchAccommodationRequest request, long stayNights) {
        if (request.getBedType() != null && room.getBedType() != request.getBedType()) {
            return false;
        }
        return isEligibleRoom(room, request.getGuestCount(), stayNights);
    }

    private boolean isEligibleRoom(Room room, int guestCount, long stayNights) {
        return room.getMaxOccupancy() >= guestCount
                && stayNights >= room.getMinStayNights()
                && stayNights <= room.getMaxStayNights();
    }

    private Optional<RoomMatch> buildRoomMatch(
            Room room,
            List<RoomRate> roomRates,
            List<RoomInventory> inventories,
            LocalDate checkInDate,
            LocalDate lastStayDate,
            long stayNights,
            BigDecimal minTotalAmount,
            BigDecimal maxTotalAmount,
            boolean excludeSoldOut
    ) {
        if (roomRates.size() != stayNights || inventories.size() != stayNights) {
            return Optional.empty();
        }
        boolean soldOut = inventories.stream()
                .anyMatch(inventory -> Boolean.TRUE.equals(inventory.getStopSale()) || inventory.getAvailableStock() <= 0);
        if (excludeSoldOut && soldOut) {
            return Optional.empty();
        }

        BigDecimal totalAmount = roomRates.stream()
                .map(RoomRate::getSalePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (minTotalAmount != null && totalAmount.compareTo(minTotalAmount) < 0) {
            return Optional.empty();
        }
        if (maxTotalAmount != null && totalAmount.compareTo(maxTotalAmount) > 0) {
            return Optional.empty();
        }

        return Optional.of(new RoomMatch(totalAmount, roomRates.getFirst().getCurrency(), room.getMaxOccupancy(), soldOut));
    }

    private Optional<CustomerAccommodationRoomResponse> buildCustomerRoomResponse(
            Room room,
            List<RoomRate> roomRates,
            List<RoomInventory> inventories,
            List<CustomerRoomImageResponse> images,
            long stayNights
    ) {
        if (roomRates.size() != stayNights || inventories.size() != stayNights) {
            return Optional.empty();
        }

        boolean soldOut = inventories.stream()
                .anyMatch(inventory -> Boolean.TRUE.equals(inventory.getStopSale()) || inventory.getAvailableStock() <= 0);
        int availableStock = inventories.stream()
                .filter(inventory -> !Boolean.TRUE.equals(inventory.getStopSale()))
                .map(RoomInventory::getAvailableStock)
                .min(Integer::compareTo)
                .orElse(0);
        java.math.BigDecimal totalAmount = roomRates.stream()
                .map(RoomRate::getSalePrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        boolean refundable = roomRates.stream().allMatch(RoomRate::getRefundable);

        return Optional.of(CustomerAccommodationRoomResponse.of(
                room,
                totalAmount,
                roomRates.getFirst().getCurrency(),
                refundable,
                soldOut ? 0 : availableStock,
                soldOut,
                images
        ));
    }

    private record RoomMatch(
            BigDecimal totalAmount,
            String currency,
            Integer maxOccupancy,
            boolean soldOut
    ) {
    }

    private record DisplayPriceSummary(
            BigDecimal amount,
            String currency
    ) {
    }
}
