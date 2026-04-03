package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.extranet.accommodation.ExtranetAccommodationDetailResponse;
import com.ota_service.ota_service.dto.extranet.accommodation.UpdateExtranetAccommodationRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomImageRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomRateRequest;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomDetailViewResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomImageResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomInventoryResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomRateResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomImagesRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomImagesResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomInventoriesRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomInventoriesResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomInventoryItemRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomRatesRequest;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomRatesResponse;
import com.ota_service.ota_service.dto.extranet.room.UpdateExtranetRoomRequest;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.ExtranetRoomRate;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomImage;
import com.ota_service.ota_service.entity.RoomInventory;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.entity.ExtranetAccommodation;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.ImageType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.AccommodationDetailRepository;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.RoomImageRepository;
import com.ota_service.ota_service.repository.RoomInventoryRepository;
import com.ota_service.ota_service.repository.RoomRateRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.repository.ExtranetAccommodationRepository;
import com.ota_service.ota_service.repository.ExtranetRoomRateRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtranetUpdateService {

    private final ExtranetAccommodationRepository extranetAccommodationRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationDetailRepository accommodationDetailRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomRateRepository roomRateRepository;
    private final ExtranetRoomRateRepository extranetRoomRateRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final RegionResolveService regionResolveService;
    private final ExtranetAccommodationService extranetAccommodationService;

    @Transactional
    public ExtranetAccommodationDetailResponse updateAccommodation(
            Long extranetId,
            String accommodationCode,
            UpdateExtranetAccommodationRequest request
    ) {
        ExtranetAccommodation extranetAccommodation = extranetAccommodationRepository.findByExtranetIdAndCode(extranetId, accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.ACCESS_DENIED, "해당 숙소를 수정할 권한이 없습니다."));
        Accommodation accommodation = accommodationRepository.findById(extranetAccommodation.getAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 숙소입니다."));

        Region region = request.addressInfo() == null ? null : regionResolveService.resolve(request.addressInfo());
        String displayAddress = request.addressInfo() == null ? accommodation.getAddress()
                : regionResolveService.resolveDisplayAddress(request.addressInfo());

        accommodation.updateExtranet(
                request.name() == null ? accommodation.getName() : request.name(),
                region == null ? accommodation.getRegionType() : resolveRegionType(region),
                request.accommodationType() == null ? accommodation.getAccommodationType() : request.accommodationType(),
                region == null ? accommodation.getRegionId() : region.getId(),
                displayAddress,
                request.addressInfo() == null ? accommodation.getLatitude() : request.addressInfo().lat(),
                request.addressInfo() == null ? accommodation.getLongitude() : request.addressInfo().lng(),
                request.thumbnailImage() == null ? accommodation.getThumbnailImage() : request.thumbnailImage(),
                request.checkInTime() == null ? accommodation.getCheckInTime() : request.checkInTime(),
                request.checkOutTime() == null ? accommodation.getCheckOutTime() : request.checkOutTime()
        );
        accommodationRepository.save(accommodation);

        AccommodationDetail detail = accommodationDetailRepository.findByAccommodationId(accommodation.getId())
                .orElseGet(() -> AccommodationDetail.of(accommodation.getId(), null, null));
        detail.update(
                request.description() == null ? detail.getDescription() : request.description(),
                request.extraInfo() == null ? detail.getExtraInfo() : request.extraInfo()
        );
        accommodationDetailRepository.save(detail);

        return extranetAccommodationService.getMyAccommodationDetail(extranetId, accommodationCode);
    }

    @Transactional
    public ExtranetRoomDetailViewResponse updateRoom(Long extranetId, String roomCode, UpdateExtranetRoomRequest request) {
        Room room = resolveOwnedRoom(extranetId, roomCode);

        room.update(
                request.name() == null ? room.getName() : request.name(),
                request.description() == null ? room.getDescription() : request.description(),
                request.standardOccupancy() == null ? room.getStandardOccupancy() : request.standardOccupancy(),
                request.maxOccupancy() == null ? room.getMaxOccupancy() : request.maxOccupancy(),
                request.bedType() == null ? room.getBedType() : request.bedType(),
                request.extraInfo() == null ? room.getExtraInfo() : request.extraInfo(),
                request.basePrice() == null ? room.getBasePrice() : request.basePrice(),
                request.currency() == null ? room.getCurrency() : request.currency(),
                request.salePrice() == null ? room.getSalePrice() : request.salePrice(),
                request.refundable() == null ? room.getRefundable() : request.refundable(),
                request.minStayNights() == null ? room.getMinStayNights() : request.minStayNights(),
                request.maxStayNights() == null ? room.getMaxStayNights() : request.maxStayNights(),
                request.defaultStock() == null ? room.getDefaultStock() : request.defaultStock()
        );
        validateRoom(room);
        roomRepository.save(room);

        return ExtranetRoomDetailViewResponse.of(room);
    }

    @Transactional
    public UpdateExtranetRoomImagesResponse updateRoomImages(
            Long extranetId,
            String roomCode,
            UpdateExtranetRoomImagesRequest request
    ) {
        Room room = resolveOwnedRoom(extranetId, roomCode);
        validateImageRequests(request.images());

        List<RoomImage> existingImages = roomImageRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscSortOrderAsc(List.of(room.getId()));
        if (!existingImages.isEmpty()) {
            LocalDateTime deletedAt = LocalDateTime.now();
            existingImages.forEach(image -> image.softDelete(deletedAt));
            roomImageRepository.saveAll(existingImages);
        }
        List<RoomImage> savedImages = roomImageRepository.saveAll(request.images().stream()
                .map(image -> RoomImage.create(room.getId(), image.imageUrl(), image.imageType(), image.sortOrder()))
                .toList());

        return UpdateExtranetRoomImagesResponse.of(
                roomCode,
                savedImages.stream().map(ExtranetRoomImageResponse::from).toList()
        );
    }

    @Transactional
    public UpdateExtranetRoomRatesResponse updateRoomRates(
            Long extranetId,
            String roomCode,
            UpdateExtranetRoomRatesRequest request
    ) {
        Room room = resolveOwnedRoom(extranetId, roomCode);
        validateRateRequests(request.rates());

        List<ExtranetRoomRate> existingRates = extranetRoomRateRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscValidFromAscValidToAsc(List.of(room.getId()));
        Map<String, ExtranetRoomRate> existingRateByKey = new HashMap<>();
        for (ExtranetRoomRate existingRate : existingRates) {
            existingRateByKey.put(rateKey(existingRate.getRateName(), existingRate.getValidFrom(), existingRate.getValidTo()), existingRate);
        }

        List<ExtranetRoomRate> ratesToSave = new ArrayList<>();
        Set<String> requestedKeys = new HashSet<>();

        for (CreateExtranetRoomRateRequest rateRequest : request.rates()) {
            String rateKey = rateKey(rateRequest.rateName(), rateRequest.validFrom(), rateRequest.validTo());
            if (!requestedKeys.add(rateKey)) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "중복된 요금 기간이 있습니다.");
            }

            ExtranetRoomRate existingRate = existingRateByKey.get(rateKey);
            if (existingRate != null) {
                RoomRate canonicalRate = roomRateRepository.findById(existingRate.getRoomRateId())
                        .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "원본 요금에 연결된 통합 요금제가 없습니다."));
                canonicalRate.update(
                        rateRequest.rateName(),
                        rateRequest.basePrice(),
                        rateRequest.currency(),
                        rateRequest.salePrice(),
                        rateRequest.refundable(),
                        rateRequest.validFrom(),
                        rateRequest.validTo()
                );
                roomRateRepository.save(canonicalRate);
                existingRate.update(
                        rateRequest.rateName(),
                        rateRequest.basePrice(),
                        rateRequest.currency(),
                        rateRequest.salePrice(),
                        rateRequest.refundable(),
                        rateRequest.validFrom(),
                        rateRequest.validTo()
                );
                ratesToSave.add(existingRate);
                continue;
            }

            RoomRate canonicalRate = roomRateRepository.save(RoomRate.create(
                    room.getId(),
                    rateRequest.rateName(),
                    rateRequest.basePrice(),
                    rateRequest.currency(),
                    rateRequest.salePrice(),
                    rateRequest.refundable(),
                    rateRequest.validFrom(),
                    rateRequest.validTo()
            ));
            ratesToSave.add(ExtranetRoomRate.create(
                    room.getId(),
                    canonicalRate.getId(),
                    rateRequest.rateName(),
                    rateRequest.basePrice(),
                    rateRequest.currency(),
                    rateRequest.salePrice(),
                    rateRequest.refundable(),
                    rateRequest.validFrom(),
                    rateRequest.validTo()
            ));
        }

        List<ExtranetRoomRate> ratesToDelete = existingRates.stream()
                .filter(existingRate -> !requestedKeys.contains(
                        rateKey(existingRate.getRateName(), existingRate.getValidFrom(), existingRate.getValidTo())
                ))
                .toList();

        if (!ratesToDelete.isEmpty()) {
            LocalDateTime deletedAt = LocalDateTime.now();
            for (ExtranetRoomRate rate : ratesToDelete) {
                rate.softDelete(deletedAt);
                RoomRate canonicalRate = roomRateRepository.findById(rate.getRoomRateId())
                        .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "원본 요금에 연결된 통합 요금제가 없습니다."));
                canonicalRate.softDelete(deletedAt);
                roomRateRepository.save(canonicalRate);
            }
            extranetRoomRateRepository.saveAll(ratesToDelete);
        }

        List<ExtranetRoomRate> savedRates = extranetRoomRateRepository.saveAll(ratesToSave);

        return UpdateExtranetRoomRatesResponse.of(
                roomCode,
                savedRates.stream().map(ExtranetRoomRateResponse::from).toList()
        );
    }

    @Transactional
    public UpdateExtranetRoomInventoriesResponse updateRoomInventories(
            Long extranetId,
            String roomCode,
            UpdateExtranetRoomInventoriesRequest request
    ) {
        Room room = resolveOwnedRoom(extranetId, roomCode);
        validateInventoryRequests(request.inventories());

        List<RoomInventory> existingInventories = roomInventoryRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscInventoryDateAsc(List.of(room.getId()));
        Map<LocalDate, RoomInventory> existingByDate = new HashMap<>();
        for (RoomInventory inventory : existingInventories) {
            existingByDate.put(inventory.getInventoryDate(), inventory);
        }

        if (!existingInventories.isEmpty()) {
            LocalDateTime deletedAt = LocalDateTime.now();
            existingInventories.forEach(inventory -> inventory.softDelete(deletedAt));
            roomInventoryRepository.saveAll(existingInventories);
        }
        List<RoomInventory> savedInventories = roomInventoryRepository.saveAll(request.inventories().stream()
                .map(inventory -> {
                    RoomInventory existing = existingByDate.get(inventory.inventoryDate());
                    int reservedStock = existing == null ? 0 : existing.getReservedStock();
                    if (inventory.totalStock() < reservedStock) {
                        throw new ApiException(ExceptionType.INVALID_INPUT, "전체 재고는 기존 예약 수량보다 작을 수 없습니다.");
                    }
                    int availableStock = inventory.totalStock() - reservedStock;
                    return RoomInventory.create(
                            room.getId(),
                            inventory.inventoryDate(),
                            inventory.totalStock(),
                            reservedStock,
                            availableStock,
                            inventory.stopSale()
                    );
                })
                .toList());

        return UpdateExtranetRoomInventoriesResponse.of(
                roomCode,
                savedInventories.stream().map(ExtranetRoomInventoryResponse::from).toList()
        );
    }

    private Room resolveOwnedRoom(Long extranetId, String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 객실입니다."));
        if (!extranetAccommodationRepository.existsByExtranetIdAndAccommodationId(extranetId, room.getAccommodationId())) {
            throw new ApiException(ExceptionType.ACCESS_DENIED, "해당 객실을 수정할 권한이 없습니다.");
        }
        return room;
    }

    private void validateRoom(Room room) {
        if (room.getMaxOccupancy() < room.getStandardOccupancy()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "최대 인원은 기준 인원보다 작을 수 없습니다.");
        }
        if (room.getMaxStayNights() < room.getMinStayNights()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 기본 최대 숙박 일수는 최소 숙박 일수보다 작을 수 없습니다.");
        }
        if (room.getBasePrice().signum() < 0 || room.getSalePrice().signum() < 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "가격은 0 이상이어야 합니다.");
        }
        if (room.getDefaultStock() < 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 기본 재고는 0 이상이어야 합니다.");
        }
    }

    private void validateImageRequests(List<CreateExtranetRoomImageRequest> images) {
        long primaryImageCount = images.stream().filter(image -> image.imageType() == ImageType.PRIMARY).count();
        if (primaryImageCount == 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 대표 이미지는 최소 1개 필요합니다.");
        }
    }

    private void validateRateRequests(List<CreateExtranetRoomRateRequest> rates) {
        for (CreateExtranetRoomRateRequest rate : rates) {
            if (rate.validTo().isBefore(rate.validFrom())) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "요금 적용 종료일은 시작일보다 빠를 수 없습니다.");
            }
        }
    }

    private String rateKey(String rateName, LocalDate validFrom, LocalDate validTo) {
        return rateName + "|" + validFrom + "|" + validTo;
    }

    private void validateInventoryRequests(List<UpdateExtranetRoomInventoryItemRequest> inventories) {
        Set<LocalDate> inventoryDates = new HashSet<>();
        for (UpdateExtranetRoomInventoryItemRequest inventory : inventories) {
            if (!inventoryDates.add(inventory.inventoryDate())) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "중복된 재고 일자가 있습니다.");
            }
        }
    }

    private AccommodationRegionType resolveRegionType(Region region) {
        return region.getCode().startsWith("KR")
                ? AccommodationRegionType.DOMESTIC
                : AccommodationRegionType.OVERSEAS;
    }
}
