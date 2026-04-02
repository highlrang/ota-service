package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.seller.accommodation.CreateSellerAccommodationRequest;
import com.ota_service.ota_service.dto.seller.accommodation.CreateSellerAccommodationResponse;
import com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationDetailResponse;
import com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationSummaryResponse;
import com.ota_service.ota_service.dto.seller.room.SellerRoomSummaryResponse;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.AccommodationDetail;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.entity.Seller;
import com.ota_service.ota_service.entity.SellerAccommodation;
import com.ota_service.ota_service.enums.AccountStatus;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.AccommodationDetailRepository;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.repository.SellerAccommodationRepository;
import com.ota_service.ota_service.repository.SellerRepository;
import com.ota_service.ota_service.util.CodeGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerAccommodationService {

    private final SellerRepository sellerRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationDetailRepository accommodationDetailRepository;
    private final RoomRepository roomRepository;
    private final SellerAccommodationRepository sellerAccommodationRepository;
    private final RegionResolveService regionResolveService;
    private final CodeGenerator codeGenerator;

    @Transactional(readOnly = true)
    public List<SellerAccommodationSummaryResponse> getMyAccommodations(Long sellerId) {
        return sellerAccommodationRepository.findSummariesBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public SellerAccommodationDetailResponse getMyAccommodationDetail(Long sellerId, String accommodationCode) {
        SellerAccommodation sellerAccommodation = sellerAccommodationRepository.findBySellerIdAndCode(sellerId, accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.ACCESS_DENIED, "해당 숙소를 조회할 권한이 없습니다."));

        Accommodation accommodation = accommodationRepository.findById(sellerAccommodation.getAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 숙소입니다."));

        AccommodationDetail detail = accommodationDetailRepository.findByAccommodationId(accommodation.getId()).orElse(null);

        List<Room> rooms = roomRepository.findAllByAccommodationIdOrderByIdAsc(accommodation.getId());
        List<SellerRoomSummaryResponse> roomResponses = rooms.stream()
                .map(SellerRoomSummaryResponse::from)
                .toList();

        return SellerAccommodationDetailResponse.of(accommodation, detail, roomResponses, sellerAccommodation);
    }

    @Transactional
    public CreateSellerAccommodationResponse create(Long sellerId, CreateSellerAccommodationRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 판매자입니다."));

        if (seller.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(ExceptionType.ACCOUNT_INACTIVE, "비활성화된 판매자 계정입니다.");
        }

        Region region = regionResolveService.resolve(request.addressInfo());
        String displayAddress = regionResolveService.resolveDisplayAddress(request.addressInfo());

        Accommodation accommodation = Accommodation.createExtranet(
                sellerId,
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

        SellerAccommodation sellerAccommodation = SellerAccommodation.of(
                sellerId,
                savedAccommodation.getId(),
                savedAccommodation.getCode()
        );
        SellerAccommodation savedSellerAccommodation = sellerAccommodationRepository.save(sellerAccommodation);

        return CreateSellerAccommodationResponse.of(savedAccommodation, detail);
    }

    private AccommodationRegionType resolveRegionType(Region region) {
        return region.getCode().startsWith("KR")
                ? AccommodationRegionType.DOMESTIC
                : AccommodationRegionType.OVERSEAS;
    }

}
