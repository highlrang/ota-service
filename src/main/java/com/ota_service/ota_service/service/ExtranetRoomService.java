package com.ota_service.ota_service.service;

import com.ota_service.ota_service.config.FileStorageProperties;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomImageRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomInventoryRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomRateRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomRequest;
import com.ota_service.ota_service.dto.extranet.room.CreateExtranetRoomResponse;
import com.ota_service.ota_service.dto.extranet.room.RoomImageUploadResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomDetailResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomDetailViewResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomImageResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomInventoryResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomRateResponse;
import com.ota_service.ota_service.entity.ExtranetRoomRate;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomImage;
import com.ota_service.ota_service.entity.RoomInventory;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.entity.ExtranetAccommodation;
import com.ota_service.ota_service.enums.ImageType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.ExtranetAccommodationRepository;
import com.ota_service.ota_service.repository.ExtranetRoomRateRepository;
import com.ota_service.ota_service.repository.RoomImageRepository;
import com.ota_service.ota_service.repository.RoomInventoryRepository;
import com.ota_service.ota_service.repository.RoomRateRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.util.CodeGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExtranetRoomService {

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final ExtranetAccommodationRepository extranetAccommodationRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final RoomRateRepository roomRateRepository;
    private final ExtranetRoomRateRepository extranetRoomRateRepository;
    private final CodeGenerator codeGenerator;
    private final FileStorageProperties fileStorageProperties;

    @Transactional(readOnly = true)
    public ExtranetRoomDetailResponse getRoomDetail(Long extranetId, String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 객실입니다."));

        if (!extranetAccommodationRepository.existsByExtranetIdAndAccommodationId(extranetId, room.getAccommodationId())) {
            throw new ApiException(ExceptionType.ACCESS_DENIED, "해당 객실을 조회할 권한이 없습니다.");
        }

        List<Long> roomIds = List.of(room.getId());
        List<RoomImage> roomImages = roomImageRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscSortOrderAsc(roomIds);
        List<ExtranetRoomRate> roomRates = extranetRoomRateRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscRateDateAsc(roomIds);
        List<RoomInventory> roomInventories = roomInventoryRepository.findAllByRoomIdInAndActiveTrueOrderByRoomIdAscInventoryDateAsc(roomIds);

        return ExtranetRoomDetailResponse.of(
                room,
                roomImages.stream().map(ExtranetRoomImageResponse::from).toList(),
                roomRates.stream().map(ExtranetRoomRateResponse::from).toList(),
                roomInventories.stream().map(ExtranetRoomInventoryResponse::from).toList(),
                room.getCreatedAt()
        );
    }

    @Transactional
    public CreateExtranetRoomResponse createRoom(Long extranetId, String accommodationCode, CreateExtranetRoomRequest request) {
        Long accommodationId = resolveAccommodationId(extranetId, accommodationCode);
        validateRoomRequest(request);

        Room room = Room.create(
                accommodationId,
                codeGenerator.generateTemporaryRoomCode(),
                request.name(),
                request.description(),
                request.standardOccupancy(),
                request.maxOccupancy(),
                request.bedType(),
                request.extraInfo(),
                request.minStayNights(),
                request.maxStayNights()
        );
        Room savedRoom = roomRepository.save(room);
        savedRoom.changeRoomCode(codeGenerator.generateRoomCode(savedRoom.getId()));
        roomRepository.save(savedRoom);
        Long savedRoomId = savedRoom.getId();

        List<RoomImage> savedImages = roomImageRepository.saveAll(
                request.images().stream()
                        .map(imageRequest -> toRoomImage(savedRoomId, imageRequest))
                        .toList()
        );

        List<CreateExtranetRoomInventoryRequest> inventories =
                request.inventories() == null ? Collections.emptyList() : request.inventories();
        List<RoomInventory> savedInventories = roomInventoryRepository.saveAll(expandRoomInventories(savedRoomId, inventories));

        List<CreateExtranetRoomRateRequest> rates = request.rates() == null ? Collections.emptyList() : request.rates();

        List<ExtranetRoomRate> savedRates = createExtranetRates(savedRoomId, rates);

        return CreateExtranetRoomResponse.of(
                savedRoom,
                savedImages.stream().map(ExtranetRoomImageResponse::from).toList(),
                savedInventories.stream().map(ExtranetRoomInventoryResponse::from).toList(),
                savedRates.stream().map(ExtranetRoomRateResponse::from).toList(),
                savedRoom.getCreatedAt()
        );
    }

    public RoomImageUploadResponse uploadRoomImage(Long extranetId, MultipartFile file) {
        if (extranetId == null) {
            throw new ApiException(ExceptionType.UNAUTHORIZED, "인증이 필요합니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "업로드할 이미지 파일이 필요합니다.");
        }
        if (!ALLOWED_IMAGE_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "지원하지 않는 이미지 형식입니다.");
        }

        String originalFilename = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "room-image";
        String extension = extractExtension(originalFilename);
        String storedFileName = UUID.randomUUID() + extension;

        Path roomImageDir = Path.of(fileStorageProperties.baseDir(), "room-images").toAbsolutePath().normalize();
        Path targetPath = roomImageDir.resolve(storedFileName).normalize();

        try {
            Files.createDirectories(roomImageDir);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "객실 이미지 업로드에 실패했습니다.");
        }

        return RoomImageUploadResponse.of(storedFileName, "/uploads/room-images/" + storedFileName);
    }

    private Long resolveAccommodationId(Long extranetId, String accommodationCode) {
        ExtranetAccommodation extranetAccommodation = extranetAccommodationRepository.findByExtranetIdAndCode(extranetId, accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.ACCESS_DENIED, "해당 숙소에 객실을 등록할 권한이 없습니다."));
        return extranetAccommodation.getAccommodationId();
    }

    private void validateRoomRequest(CreateExtranetRoomRequest request) {
        if (request.maxOccupancy() < request.standardOccupancy()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "최대 인원은 기준 인원보다 작을 수 없습니다.");
        }
        if (request.maxStayNights() < request.minStayNights()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 기본 최대 숙박 일수는 최소 숙박 일수보다 작을 수 없습니다.");
        }
        validateImageRequests(request.images());
        validateInventoryRequests(request.inventories());
        validateRateRequests(request.rates());
    }

    private void validateImageRequests(List<CreateExtranetRoomImageRequest> images) {
        long primaryImageCount = images.stream()
                .filter(image -> image.imageType() == ImageType.PRIMARY)
                .count();
        if (primaryImageCount == 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 대표 이미지는 최소 1개 필요합니다.");
        }
    }

    private void validateInventoryRequests(List<CreateExtranetRoomInventoryRequest> inventories) {
        Set<LocalDate> inventoryDates = new HashSet<>();
        for (CreateExtranetRoomInventoryRequest inventory : inventories) {
            if (inventory.validTo().isBefore(inventory.validFrom())) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "재고 적용 종료일은 시작일보다 빠를 수 없습니다.");
            }
            LocalDate date = inventory.validFrom();
            while (!date.isAfter(inventory.validTo())) {
                if (!inventoryDates.add(date)) {
                    throw new ApiException(ExceptionType.INVALID_INPUT, "중복된 재고 일자가 있습니다.");
                }
                date = date.plusDays(1);
            }
        }
    }

    private void validateRateRequests(List<CreateExtranetRoomRateRequest> rates) {
        Set<LocalDate> rateDates = new HashSet<>();
        for (CreateExtranetRoomRateRequest rate : rates) {
            if (rate.validTo().isBefore(rate.validFrom())) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "요금 적용 종료일은 시작일보다 빠를 수 없습니다.");
            }
            LocalDate date = rate.validFrom();
            while (!date.isAfter(rate.validTo())) {
                if (!rateDates.add(date)) {
                    throw new ApiException(ExceptionType.INVALID_INPUT, "중복된 요금 일자가 있습니다.");
                }
                date = date.plusDays(1);
            }
        }
    }

    private RoomImage toRoomImage(Long roomId, CreateExtranetRoomImageRequest request) {
        return RoomImage.create(
                roomId,
                request.imageUrl(),
                request.imageType(),
                request.sortOrder()
        );
    }

    private List<ExtranetRoomRate> createExtranetRates(Long roomId, List<CreateExtranetRoomRateRequest> requests) {
        List<ExtranetRoomRate> savedRates = new ArrayList<>();
        for (CreateExtranetRoomRateRequest request : requests) {
            LocalDate date = request.validFrom();
            while (!date.isAfter(request.validTo())) {
                RoomRate canonicalRate = roomRateRepository.save(RoomRate.create(
                        roomId,
                        request.rateName(),
                        request.basePrice(),
                        request.currency(),
                        request.salePrice(),
                        request.refundable(),
                        date
                ));
                savedRates.add(extranetRoomRateRepository.save(ExtranetRoomRate.create(
                        roomId,
                        canonicalRate.getId(),
                        request.rateName(),
                        request.basePrice(),
                        request.currency(),
                        request.salePrice(),
                        request.refundable(),
                        date
                )));
                date = date.plusDays(1);
            }
        }
        return savedRates;
    }

    private List<RoomInventory> expandRoomInventories(Long roomId, List<CreateExtranetRoomInventoryRequest> requests) {
        List<RoomInventory> inventories = new ArrayList<>();
        for (CreateExtranetRoomInventoryRequest request : requests) {
            LocalDate date = request.validFrom();
            while (!date.isAfter(request.validTo())) {
                inventories.add(RoomInventory.create(
                        roomId,
                        date,
                        request.totalStock(),
                        0,
                        request.stopSale() ? 0 : request.totalStock(),
                        request.stopSale()
                ));
                date = date.plusDays(1);
            }
        }
        return inventories;
    }

    private String extractExtension(String fileName) {
        int extensionStartIndex = fileName.lastIndexOf('.');
        if (extensionStartIndex < 0) {
            return "";
        }
        return fileName.substring(extensionStartIndex);
    }
}
