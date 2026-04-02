package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationDetailResponse;
import com.ota_service.ota_service.dto.seller.accommodation.UpdateSellerAccommodationRequest;
import com.ota_service.ota_service.dto.seller.room.CreateSellerRoomImageRequest;
import com.ota_service.ota_service.dto.seller.room.CreateSellerRoomRateRequest;
import com.ota_service.ota_service.dto.seller.room.SellerRoomDetailViewResponse;
import com.ota_service.ota_service.dto.seller.room.SellerRoomImageResponse;
import com.ota_service.ota_service.dto.seller.room.SellerRoomInventoryResponse;
import com.ota_service.ota_service.dto.seller.room.SellerRoomRateResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomImagesRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomImagesResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomInventoriesRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomInventoriesResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomInventoryItemRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomRatesRequest;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomRatesResponse;
import com.ota_service.ota_service.dto.seller.room.UpdateSellerRoomRequest;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomImage;
import com.ota_service.ota_service.entity.RoomInventory;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.entity.SellerAccommodation;
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
import com.ota_service.ota_service.repository.SellerAccommodationRepository;
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
public class SellerUpdateService {

    private final SellerAccommodationRepository sellerAccommodationRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationDetailRepository accommodationDetailRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomRateRepository roomRateRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final RegionResolveService regionResolveService;
    private final SellerAccommodationService sellerAccommodationService;

    @Transactional
    public SellerAccommodationDetailResponse updateAccommodation(
            Long sellerId,
            String accommodationCode,
            UpdateSellerAccommodationRequest request
    ) {
        SellerAccommodation sellerAccommodation = sellerAccommodationRepository.findBySellerIdAndCode(sellerId, accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.ACCESS_DENIED, "해당 숙소를 수정할 권한이 없습니다."));
        Accommodation accommodation = accommodationRepository.findById(sellerAccommodation.getAccommodationId())
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

        return sellerAccommodationService.getMyAccommodationDetail(sellerId, accommodationCode);
    }

    @Transactional
    public SellerRoomDetailViewResponse updateRoom(Long sellerId, String roomCode, UpdateSellerRoomRequest request) {
        Room room = resolveOwnedRoom(sellerId, roomCode);

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

        return SellerRoomDetailViewResponse.of(room);
    }

    @Transactional
    public UpdateSellerRoomImagesResponse updateRoomImages(
            Long sellerId,
            String roomCode,
            UpdateSellerRoomImagesRequest request
    ) {
        Room room = resolveOwnedRoom(sellerId, roomCode);
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

        return UpdateSellerRoomImagesResponse.of(
                roomCode,
                savedImages.stream().map(SellerRoomImageResponse::from).toList()
        );
    }

    @Transactional
    public UpdateSellerRoomRatesResponse updateRoomRates(
            Long sellerId,
            String roomCode,
            UpdateSellerRoomRatesRequest request
    ) {
        Room room = resolveOwnedRoom(sellerId, roomCode);
        validateRateRequests(request.rates());

        List<RoomRate> existingRates = roomRateRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscValidFromAscValidToAsc(List.of(room.getId()));
        Map<String, RoomRate> existingRateByKey = new HashMap<>();
        for (RoomRate existingRate : existingRates) {
            existingRateByKey.put(rateKey(existingRate.getRateName(), existingRate.getValidFrom(), existingRate.getValidTo()), existingRate);
        }

        List<RoomRate> ratesToSave = new ArrayList<>();
        Set<String> requestedKeys = new HashSet<>();

        for (CreateSellerRoomRateRequest rateRequest : request.rates()) {
            String rateKey = rateKey(rateRequest.rateName(), rateRequest.validFrom(), rateRequest.validTo());
            if (!requestedKeys.add(rateKey)) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "중복된 요금 기간이 있습니다.");
            }

            RoomRate existingRate = existingRateByKey.get(rateKey);
            if (existingRate != null) {
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

            ratesToSave.add(RoomRate.create(
                    room.getId(),
                    rateRequest.rateName(),
                    rateRequest.basePrice(),
                    rateRequest.currency(),
                    rateRequest.salePrice(),
                    rateRequest.refundable(),
                    rateRequest.validFrom(),
                    rateRequest.validTo()
            ));
        }

        List<RoomRate> ratesToDelete = existingRates.stream()
                .filter(existingRate -> !requestedKeys.contains(
                        rateKey(existingRate.getRateName(), existingRate.getValidFrom(), existingRate.getValidTo())
                ))
                .toList();

        if (!ratesToDelete.isEmpty()) {
            LocalDateTime deletedAt = LocalDateTime.now();
            ratesToDelete.forEach(rate -> rate.softDelete(deletedAt));
            roomRateRepository.saveAll(ratesToDelete);
        }

        List<RoomRate> savedRates = roomRateRepository.saveAll(ratesToSave);

        return UpdateSellerRoomRatesResponse.of(
                roomCode,
                savedRates.stream().map(SellerRoomRateResponse::from).toList()
        );
    }

    @Transactional
    public UpdateSellerRoomInventoriesResponse updateRoomInventories(
            Long sellerId,
            String roomCode,
            UpdateSellerRoomInventoriesRequest request
    ) {
        Room room = resolveOwnedRoom(sellerId, roomCode);
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

        return UpdateSellerRoomInventoriesResponse.of(
                roomCode,
                savedInventories.stream().map(SellerRoomInventoryResponse::from).toList()
        );
    }

    private Room resolveOwnedRoom(Long sellerId, String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 객실입니다."));
        if (!sellerAccommodationRepository.existsBySellerIdAndAccommodationId(sellerId, room.getAccommodationId())) {
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

    private void validateImageRequests(List<CreateSellerRoomImageRequest> images) {
        long primaryImageCount = images.stream().filter(image -> image.imageType() == ImageType.PRIMARY).count();
        if (primaryImageCount == 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 대표 이미지는 최소 1개 필요합니다.");
        }
    }

    private void validateRateRequests(List<CreateSellerRoomRateRequest> rates) {
        for (CreateSellerRoomRateRequest rate : rates) {
            if (rate.validTo().isBefore(rate.validFrom())) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "요금 적용 종료일은 시작일보다 빠를 수 없습니다.");
            }
        }
    }

    private String rateKey(String rateName, LocalDate validFrom, LocalDate validTo) {
        return rateName + "|" + validFrom + "|" + validTo;
    }

    private void validateInventoryRequests(List<UpdateSellerRoomInventoryItemRequest> inventories) {
        Set<LocalDate> inventoryDates = new HashSet<>();
        for (UpdateSellerRoomInventoryItemRequest inventory : inventories) {
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
