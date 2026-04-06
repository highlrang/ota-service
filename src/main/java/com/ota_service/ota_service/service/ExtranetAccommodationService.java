package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.extranet.accommodation.CreateExtranetAccommodationRequest;
import com.ota_service.ota_service.dto.extranet.accommodation.CreateExtranetAccommodationResponse;
import com.ota_service.ota_service.dto.extranet.accommodation.ExtranetAccommodationDetailResponse;
import com.ota_service.ota_service.dto.extranet.accommodation.ExtranetAccommodationSummaryResponse;
import com.ota_service.ota_service.dto.extranet.room.ExtranetRoomSummaryResponse;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.entity.Extranet;
import com.ota_service.ota_service.entity.ExtranetAccommodation;
import com.ota_service.ota_service.enums.AccountStatus;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.AccommodationDetailRepository;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.repository.ExtranetAccommodationRepository;
import com.ota_service.ota_service.repository.ExtranetRepository;
import com.ota_service.ota_service.util.CodeGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtranetAccommodationService {

    private final ExtranetRepository extranetRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationDetailRepository accommodationDetailRepository;
    private final RoomRepository roomRepository;
    private final ExtranetAccommodationRepository extranetAccommodationRepository;
    private final RegionResolveService regionResolveService;
    private final CodeGenerator codeGenerator;

    @Transactional(readOnly = true)
    public List<ExtranetAccommodationSummaryResponse> getMyAccommodations(Long extranetId) {
        return extranetAccommodationRepository.findSummariesByExtranetId(extranetId);
    }

    @Transactional(readOnly = true)
    public ExtranetAccommodationDetailResponse getMyAccommodationDetail(Long extranetId, String accommodationCode) {
        ExtranetAccommodation extranetAccommodation = extranetAccommodationRepository.findByExtranetIdAndCode(extranetId, accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.ACCESS_DENIED, "해당 숙소를 조회할 권한이 없습니다."));

        Accommodation accommodation = accommodationRepository.findById(extranetAccommodation.getAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 숙소입니다."));

        AccommodationDetail detail = accommodationDetailRepository.findByAccommodationId(accommodation.getId()).orElse(null);

        List<Room> rooms = roomRepository.findAllByAccommodationIdOrderByIdAsc(accommodation.getId());
        List<ExtranetRoomSummaryResponse> roomResponses = rooms.stream()
                .map(ExtranetRoomSummaryResponse::from)
                .toList();

        return ExtranetAccommodationDetailResponse.of(accommodation, detail, roomResponses, extranetAccommodation);
    }

    @Transactional
    public CreateExtranetAccommodationResponse create(Long extranetId, CreateExtranetAccommodationRequest request) {
        Extranet extranet = extranetRepository.findById(extranetId)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 판매자입니다."));

        if (extranet.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(ExceptionType.ACCOUNT_INACTIVE, "비활성화된 판매자 계정입니다.");
        }

        Region region = regionResolveService.resolve(request.addressInfo());
        String displayAddress = regionResolveService.resolveDisplayAddress(request.addressInfo());

        Accommodation accommodation = Accommodation.createExtranet(
                extranetId,
                request.name(),
                resolveRegionType(region),
                request.accommodationType(),
                region.getId(),
                displayAddress,
                request.addressInfo().lat(),
                request.addressInfo().lng(),
                request.thumbnailImage(),
                request.checkInTime(),
                request.checkOutTime(),
                codeGenerator.generateTemporaryAccommodationCode()
        );
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        String accommodationCode = codeGenerator.generateAccommodationCode(savedAccommodation.getId());
        savedAccommodation.setCode(accommodationCode);
        savedAccommodation = accommodationRepository.save(savedAccommodation);

        AccommodationDetail detail = AccommodationDetail.of(
                savedAccommodation.getId(),
                request.description(),
                request.extraInfo()
        );
        accommodationDetailRepository.save(detail);

        ExtranetAccommodation extranetAccommodation = ExtranetAccommodation.of(
                extranetId,
                savedAccommodation.getId(),
                savedAccommodation.getCode()
        );
        ExtranetAccommodation savedExtranetAccommodation = extranetAccommodationRepository.save(extranetAccommodation);

        return CreateExtranetAccommodationResponse.of(savedAccommodation, detail);
    }

    private AccommodationRegionType resolveRegionType(Region region) {
        return region.getCode().startsWith("KR")
                ? AccommodationRegionType.DOMESTIC
                : AccommodationRegionType.OVERSEAS;
    }

}
