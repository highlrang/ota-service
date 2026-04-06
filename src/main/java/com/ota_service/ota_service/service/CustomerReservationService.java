package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.customer.reservation.CancelCustomerReservationRequest;
import com.ota_service.ota_service.dto.customer.reservation.CancelCustomerReservationResponse;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationRequest;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationResponse;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationDetailResponse;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationItemResponse;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationListType;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationPageResponse;
import com.ota_service.ota_service.dto.customer.reservation.CustomerReservationPaymentResponse;
import com.ota_service.ota_service.dto.customer.reservation.PaymentApprovalRequest;
import com.ota_service.ota_service.dto.customer.reservation.PaymentApprovalResponse;
import com.ota_service.ota_service.dto.customer.reservation.PaymentRefundRequest;
import com.ota_service.ota_service.dto.customer.reservation.PaymentRefundResponse;
import com.ota_service.ota_service.dto.supplier.onda.OndaCancelReservationRequest;
import com.ota_service.ota_service.dto.supplier.onda.OndaCreateReservationRequest;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.Payment;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.entity.Refund;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomInventory;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.entity.SupplierAccommodation;
import com.ota_service.ota_service.entity.SupplierRatePlan;
import com.ota_service.ota_service.entity.SupplierRoom;
import com.ota_service.ota_service.entity.User;
import com.ota_service.ota_service.enums.AccommodationSourceType;
import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.BusinessStatus;
import com.ota_service.ota_service.enums.ReservationStatus;
import com.ota_service.ota_service.enums.SupplierSourceType;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.SupplierAccommodationRepository;
import com.ota_service.ota_service.repository.SupplierRatePlanRepository;
import com.ota_service.ota_service.repository.SupplierRoomRepository;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.PaymentRepository;
import com.ota_service.ota_service.repository.ReservationRepository;
import com.ota_service.ota_service.repository.RefundRepository;
import com.ota_service.ota_service.repository.RoomInventoryRepository;
import com.ota_service.ota_service.repository.RoomRateRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.repository.UserRepository;
import com.ota_service.ota_service.util.CodeGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {

    private static final SupplierSourceType ONDA_SOURCE = SupplierSourceType.ONDA;
    private static final String DEFAULT_BOOKER_EMAIL_DOMAIN = "ota.local";
    private static final String DEFAULT_NATIONALITY = "KR";
    private static final String DEFAULT_TIMEZONE = "Asia/Seoul";

    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final RoomRateRepository roomRateRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final SupplierAccommodationRepository supplierAccommodationRepository;
    private final SupplierRoomRepository supplierRoomRepository;
    private final SupplierRatePlanRepository supplierRatePlanRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayClient paymentGatewayClient;
    private final OndaSupplierReservationClient ondaSupplierReservationClient;
    private final CodeGenerator codeGenerator;

    @Transactional
    public CreateCustomerReservationResponse createReservation(
            Long customerId,
            CreateCustomerReservationRequest request
    ) {
        // 1. 숙소/객실/인원/재고/요금과 ONDA 연동 대상을 검증한다.
        ReservationPreparation preparation = prepareReservationCreation(request);
        LocalDateTime now = LocalDateTime.now();
        // 2. 내부 예약 엔티티를 먼저 생성해 예약 번호를 확보한다.
        Reservation reservation = createConfirmedReservation(customerId, request, preparation, now);
        // 3. ONDA 대상 숙소면 외부 예약을 먼저 생성한다.
        syncOndaReservationIfNeeded(customerId, request, reservation, preparation);
        // 4. 결제를 승인하고, 실패 시 생성된 ONDA 예약을 자동 취소한다.
        Payment payment = approveAndCreatePaymentWithOndaCompensation(request, reservation, preparation, now);
        // 5. 최종 성공 시 내부 재고를 차감한다.
        applyReservationInventories(preparation.inventories());

        return CreateCustomerReservationResponse.from(
                reservation,
                preparation.accommodation(),
                preparation.room(),
                payment
        );
    }

    private ReservationPreparation prepareReservationCreation(CreateCustomerReservationRequest request) {
        validateStayPeriod(request.checkInAt(), request.checkOutAt());
        LocalDate checkInDate = request.checkInAt().toLocalDate();
        LocalDate checkOutDate = request.checkOutAt().toLocalDate();

        Accommodation accommodation = findReservationAccommodation(request.accommodationCode());
        Room room = findReservationRoom(accommodation, request.roomCode());
        GuestCounts guestCounts = resolveGuestCounts(request, room, checkInDate, checkOutDate);
        List<RoomRate> roomRates = loadReservationRoomRates(room, checkInDate, checkOutDate, request.paymentAmount());
        List<RoomInventory> inventories = loadReservationInventories(accommodation, room, checkInDate, checkOutDate);
        OndaReservationSyncTarget ondaSyncTarget = validateAndResolveOndaSyncTarget(
                accommodation,
                roomRates.getFirst(),
                checkInDate,
                checkOutDate
        );

        return new ReservationPreparation(
                accommodation,
                room,
                guestCounts,
                roomRates,
                inventories,
                ondaSyncTarget
        );
    }

    private Accommodation findReservationAccommodation(String accommodationCode) {
        Accommodation accommodation = accommodationRepository.findByCode(accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 숙소 코드입니다."));
        validateAccommodation(accommodation);
        return accommodation;
    }

    private Room findReservationRoom(Accommodation accommodation, String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 객실 코드입니다."));
        validateRoom(accommodation, room);
        return room;
    }

    private GuestCounts resolveGuestCounts(
            CreateCustomerReservationRequest request,
            Room room,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        int adultCount = request.adultCount() == null ? 1 : request.adultCount();
        int childCount = request.childCount() == null ? 0 : request.childCount();
        validateOccupancy(room, adultCount, childCount);
        validateStayNights(room, checkInDate, checkOutDate);
        return new GuestCounts(adultCount, childCount);
    }

    private List<RoomRate> loadReservationRoomRates(
            Room room,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            BigDecimal paymentAmount
    ) {
        List<RoomRate> roomRates = roomRateRepository.findAllByRoomIdAndActiveTrueAndRateDateBetweenOrderByRateDateAsc(
                room.getId(),
                checkInDate,
                checkOutDate.minusDays(1)
        );
        validateRoomRates(checkInDate, checkOutDate, roomRates);
        validatePaymentAmount(paymentAmount, roomRates);
        return roomRates;
    }

    private List<RoomInventory> loadReservationInventories(
            Accommodation accommodation,
            Room room,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        if (!shouldManageInternalInventory(accommodation)) {
            return List.of();
        }
        List<RoomInventory> inventories = roomInventoryRepository.findAllByRoomIdAndInventoryDateBetweenAndActiveTrueForUpdate(
                room.getId(),
                checkInDate,
                checkOutDate.minusDays(1)
        );
        validateInventories(checkInDate, checkOutDate, inventories);
        return inventories;
    }

    private OndaReservationSyncTarget validateAndResolveOndaSyncTarget(
            Accommodation accommodation,
            RoomRate representativeRoomRate,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        OndaReservationSyncTarget ondaSyncTarget = resolveOndaSyncTarget(accommodation, representativeRoomRate);
        if (ondaSyncTarget != null) {
            validateOndaAvailability(ondaSyncTarget, checkInDate, checkOutDate);
        }
        return ondaSyncTarget;
    }

    private Reservation createConfirmedReservation(
            Long customerId,
            CreateCustomerReservationRequest request,
            ReservationPreparation preparation,
            LocalDateTime now
    ) {
        Reservation reservation = Reservation.createConfirmed(
                customerId,
                preparation.accommodation().getId(),
                preparation.room().getId(),
                preparation.roomRates().getFirst().getId(),
                "TMP-RSV-" + now.toLocalDate() + "-" + System.nanoTime(),
                request.checkInAt(),
                request.checkOutAt(),
                request.guestName(),
                request.guestPhoneNumber(),
                preparation.guestCounts().adultCount(),
                preparation.guestCounts().childCount(),
                request.paymentAmount(),
                now
        );
        reservationRepository.save(reservation);
        reservation.changeReservationNo(codeGenerator.generateReservationCode(LocalDate.now(), reservation.getId()));
        return reservation;
    }

    private Payment approveAndCreatePayment(
            CreateCustomerReservationRequest request,
            List<RoomRate> roomRates,
            Reservation reservation,
            LocalDateTime now
    ) {
        PaymentApprovalResponse paymentApproval = paymentGatewayClient.approve(new PaymentApprovalRequest(
                reservation.getReservationNo(),
                request.paymentMethod(),
                request.paymentAmount(),
                roomRates.getFirst().getCurrency()
        ));
        if (!paymentApproval.approved()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "결제 승인에 실패했습니다.");
        }

        Payment payment = Payment.createPaid(
                reservation.getId(),
                "TMP-PAY-" + now.toLocalDate() + "-" + System.nanoTime(),
                request.paymentMethod(),
                request.paymentAmount(),
                roomRates.getFirst().getCurrency(),
                paymentApproval.approvedAt()
        );
        paymentRepository.save(payment);
        payment.changePaymentNo(codeGenerator.generatePaymentCode(LocalDate.now(), payment.getId()));
        return payment;
    }

    private Payment approveAndCreatePaymentWithOndaCompensation(
            CreateCustomerReservationRequest request,
            Reservation reservation,
            ReservationPreparation preparation,
            LocalDateTime now
    ) {
        try {
            return approveAndCreatePayment(request, preparation.roomRates(), reservation, now);
        } catch (RuntimeException exception) {
            rollbackOndaReservationIfNeeded(reservation, preparation);
            throw exception;
        }
    }

    private void syncOndaReservationIfNeeded(
            Long customerId,
            CreateCustomerReservationRequest request,
            Reservation reservation,
            ReservationPreparation preparation
    ) {
        if (preparation.ondaSyncTarget() == null) {
            return;
        }

        String supplierBookingNumber = createOndaReservation(
                customerId,
                request,
                reservation,
                preparation.roomRates(),
                preparation.ondaSyncTarget()
        );
        reservation.changeSupplierBookingNumber(supplierBookingNumber);
    }

    private void applyReservationInventories(List<RoomInventory> inventories) {
        for (RoomInventory inventory : inventories) {
            inventory.setReservedStock(inventory.getReservedStock() + 1);
            inventory.setAvailableStock(inventory.getAvailableStock() - 1);
        }
    }

    @Transactional
    public CancelCustomerReservationResponse cancelReservation(
            Long customerId,
            String guestName,
            String guestPhoneNumber,
            String reservationNo,
            CancelCustomerReservationRequest request
    ) {
        Reservation reservation = findAccessibleReservation(customerId, guestName, guestPhoneNumber, reservationNo);
        validateCancelableReservation(reservation);

        Accommodation accommodation = accommodationRepository.findById(reservation.getAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "예약에 연결된 숙소 정보가 존재하지 않습니다."));

        Payment payment = paymentRepository.findByReservationId(reservation.getId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "예약에 연결된 결제 정보가 존재하지 않습니다."));
        if (refundRepository.findByPaymentId(payment.getId()).isPresent()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "이미 환불 처리된 예약입니다.");
        }

        OndaReservationSyncTarget ondaSyncTarget = resolveOndaSyncTarget(accommodation, reservation.getRoomRateId());
        if (ondaSyncTarget != null) {
            cancelOndaReservation(reservation, payment, request, ondaSyncTarget);
        }

        if (shouldManageInternalInventory(accommodation)) {
            LocalDate checkInDate = reservation.getCheckInAt().toLocalDate();
            LocalDate checkOutDate = reservation.getCheckOutAt().toLocalDate();
            List<RoomInventory> inventories = roomInventoryRepository.findAllByRoomIdAndInventoryDateBetweenAndActiveTrueForUpdate(
                    reservation.getRoomId(),
                    checkInDate,
                    checkOutDate.minusDays(1)
            );
            validateInventoriesForRestore(checkInDate, checkOutDate, inventories);

            for (RoomInventory inventory : inventories) {
                inventory.setReservedStock(Math.max(0, inventory.getReservedStock() - 1));
                inventory.setAvailableStock(inventory.getAvailableStock() + 1);
            }
        }

        PaymentRefundResponse paymentRefund = paymentGatewayClient.refund(new PaymentRefundRequest(
                reservation.getReservationNo(),
                payment.getPaymentNo(),
                payment.getPaymentAmount(),
                request == null ? null : request.reason()
        ));
        if (!paymentRefund.refunded()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "결제 환불에 실패했습니다.");
        }

        LocalDateTime cancelledAt = paymentRefund.refundedAt();
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(cancelledAt);
        payment.setPaymentStatus(com.ota_service.ota_service.enums.PaymentStatus.REFUNDED);

        Refund refund = Refund.create(
                payment.getId(),
                "TMP-RFD-" + cancelledAt.toLocalDate() + "-" + System.nanoTime(),
                payment.getPaymentAmount(),
                request == null ? null : request.reason(),
                cancelledAt
        );
        refundRepository.save(refund);
        refund.changeRefundNo(codeGenerator.generateRefundCode(cancelledAt.toLocalDate(), refund.getId()));

        return CancelCustomerReservationResponse.from(reservation, payment, refund);
    }

    @Transactional(readOnly = true)
    public CustomerReservationPageResponse getReservations(
            Long customerId,
            String guestName,
            String guestPhoneNumber,
            CustomerReservationListType type,
            Pageable pageable
    ) {
        List<Reservation> reservations = loadAccessibleReservations(customerId, guestName, guestPhoneNumber);
        if (reservations.isEmpty()) {
            return CustomerReservationPageResponse.empty(pageable);
        }

        Map<Long, Accommodation> accommodationMap = getAccommodationMap(reservations);

        LocalDate today = LocalDate.now();
        List<CustomerReservationItemResponse> filteredReservations = reservations.stream()
                .filter(reservation -> isDomesticReservation(reservation, accommodationMap))
                .filter(reservation -> matchesListType(reservation, type, today))
                .map(reservation -> CustomerReservationItemResponse.of(
                        reservation,
                        accommodationMap.get(reservation.getAccommodationId())
                ))
                .toList();
        return toPageResponse(filteredReservations, pageable);
    }

    @Transactional(readOnly = true)
    public CustomerReservationDetailResponse getReservationDetail(
            Long customerId,
            String guestName,
            String guestPhoneNumber,
            String reservationNo
    ) {
        Reservation reservation = findAccessibleReservation(customerId, guestName, guestPhoneNumber, reservationNo);
        Accommodation accommodation = accommodationRepository.findById(reservation.getAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "예약에 연결된 숙소 정보가 존재하지 않습니다."));
        if (accommodation.getRegionType() != AccommodationRegionType.DOMESTIC) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "국내 숙소 예약 내역만 조회할 수 있습니다.");
        }

        Payment payment = paymentRepository.findByReservationId(reservation.getId())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "예약에 연결된 결제 정보가 존재하지 않습니다."));

        return CustomerReservationDetailResponse.of(
                reservation,
                accommodation,
                CustomerReservationPaymentResponse.from(payment)
        );
    }

    private void validateOndaAvailability(
            OndaReservationSyncTarget ondaSyncTarget,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        var availability = ondaSupplierReservationClient.checkAvailability(
                ondaSyncTarget.propertyId(),
                ondaSyncTarget.roomtypeId(),
                ondaSyncTarget.rateplanId(),
                checkInDate,
                checkOutDate
        );
        if (!availability.availability()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "ONDA 재고 확인 결과 예약 가능한 객실이 없습니다.");
        }
    }

    private String createOndaReservation(
            Long customerId,
            CreateCustomerReservationRequest request,
            Reservation reservation,
            List<RoomRate> roomRates,
            OndaReservationSyncTarget ondaSyncTarget
    ) {
        BigDecimal totalAmount = roomRates.stream()
                .map(RoomRate::getSalePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String bookerEmail = resolveBookerEmail(customerId, reservation.getReservationNo());
        String bookerName = resolveBookerName(customerId, request.guestName());
        String bookerPhone = resolveBookerPhone(customerId, request.guestPhoneNumber());
        OndaCreateReservationRequest createRequest = OndaCreateReservationRequest.forReservation(
                reservation,
                roomRates.getFirst().getCurrency(),
                ondaSyncTarget.roomtypeId(),
                ondaSyncTarget.rateplanId(),
                toIntegerAmount(totalAmount),
                bookerName,
                bookerEmail,
                bookerPhone,
                DEFAULT_NATIONALITY,
                DEFAULT_TIMEZONE
        );

        var response = ondaSupplierReservationClient.createReservation(ondaSyncTarget.propertyId(), createRequest);
        if (response.bookingNumber() == null || response.bookingNumber().isBlank()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "ONDA 예약 생성 응답에 예약 번호가 없습니다.");
        }
        return response.bookingNumber();
    }

    private void cancelOndaReservation(
            Reservation reservation,
            Payment payment,
            CancelCustomerReservationRequest request,
            OndaReservationSyncTarget ondaSyncTarget
    ) {
        if (reservation.getSupplierBookingNumber() == null || reservation.getSupplierBookingNumber().isBlank()) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "공급사 예약 번호가 없어 ONDA 예약 취소를 진행할 수 없습니다.");
        }

        ondaSupplierReservationClient.cancelReservation(
                ondaSyncTarget.propertyId(),
                reservation.getSupplierBookingNumber(),
                OndaCancelReservationRequest.forUserCancel(
                        request,
                        payment,
                        toIntegerAmount(payment.getPaymentAmount())
                )
        );
    }

    private void rollbackOndaReservationIfNeeded(Reservation reservation, ReservationPreparation preparation) {
        if (preparation.ondaSyncTarget() == null) {
            return;
        }
        if (reservation.getSupplierBookingNumber() == null || reservation.getSupplierBookingNumber().isBlank()) {
            return;
        }

        BigDecimal totalAmount = preparation.roomRates().stream()
                .map(RoomRate::getSalePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String reason = "결제 실패로 인한 시스템 자동 취소";

        try {
            ondaSupplierReservationClient.cancelReservation(
                    preparation.ondaSyncTarget().propertyId(),
                    reservation.getSupplierBookingNumber(),
                    OndaCancelReservationRequest.forSystemRollback(
                            preparation.roomRates().getFirst().getCurrency(),
                            toIntegerAmount(totalAmount),
                            reason
                    )
            );
        } catch (RuntimeException rollbackException) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR,
                    "결제 실패 후 ONDA 예약 자동 취소에 실패했습니다. reservationNo=" + reservation.getReservationNo());
        }
    }

    private OndaReservationSyncTarget resolveOndaSyncTarget(Accommodation accommodation, RoomRate roomRate) {
        return resolveOndaSyncTarget(accommodation, roomRate.getId());
    }

    private OndaReservationSyncTarget resolveOndaSyncTarget(Accommodation accommodation, Long roomRateId) {
        if (!shouldSyncToOnda(accommodation)) {
            return null;
        }

        String propertyId = accommodation.getSupplierProductId();
        if (propertyId == null || propertyId.isBlank()) {
            SupplierAccommodation supplierAccommodation = supplierAccommodationRepository
                    .findBySourceAndAccommodationId(ONDA_SOURCE, accommodation.getId())
                    .orElseThrow(() -> new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "ONDA 숙소 매핑 정보를 찾을 수 없습니다."));
            propertyId = supplierAccommodation.getSupplierPropertyId();
        }

        ReservationRatePlanMapping mapping = findOndaRatePlanMapping(accommodation.getId(), roomRateId);
        return new OndaReservationSyncTarget(propertyId, mapping.roomtypeId(), mapping.rateplanId());
    }

    private boolean shouldSyncToOnda(Accommodation accommodation) {
        return accommodation.getSourceType() == AccommodationSourceType.SUPPLIER
                || (accommodation.getSupplierProductId() != null && !accommodation.getSupplierProductId().isBlank());
    }

    private boolean shouldManageInternalInventory(Accommodation accommodation) {
        return accommodation.getSupplierProductId() == null || accommodation.getSupplierProductId().isBlank();
    }

    private ReservationRatePlanMapping findOndaRatePlanMapping(Long accommodationId, Long roomRateId) {
        SupplierRatePlan supplierRatePlan = supplierRatePlanRepository.findBySourceAndRoomRateId(ONDA_SOURCE, roomRateId)
                .orElseThrow(() -> new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "ONDA 요금제 매핑 정보를 찾을 수 없습니다."));
        SupplierRoom supplierRoom = supplierRoomRepository.findById(supplierRatePlan.getSupplierRoomId())
                .orElseThrow(() -> new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "ONDA 객실 매핑 정보를 찾을 수 없습니다."));
        SupplierAccommodation supplierAccommodation = supplierAccommodationRepository.findById(supplierRoom.getSupplierAccommodationId())
                .orElseThrow(() -> new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "ONDA 숙소 매핑 정보를 찾을 수 없습니다."));

        if (!supplierAccommodation.getAccommodationId().equals(accommodationId)) {
            throw new ApiException(ExceptionType.INTERNAL_SERVER_ERROR, "ONDA 숙소/객실/요금제 매핑 관계가 일치하지 않습니다.");
        }
        return new ReservationRatePlanMapping(
                supplierRoom.getSupplierRoomtypeId(),
                supplierRatePlan.getSupplierRateplanId()
        );
    }

    private String resolveBookerEmail(Long customerId, String reservationNo) {
        return loadUser(customerId)
                .map(User::getEmail)
                .filter(email -> !email.isBlank())
                .orElse("guest+" + reservationNo + "@" + DEFAULT_BOOKER_EMAIL_DOMAIN);
    }

    private String resolveBookerName(Long customerId, String fallbackName) {
        return loadUser(customerId)
                .map(User::getName)
                .filter(name -> !name.isBlank())
                .orElse(fallbackName);
    }

    private String resolveBookerPhone(Long customerId, String fallbackPhone) {
        return loadUser(customerId)
                .map(User::getPhoneNumber)
                .filter(phone -> !phone.isBlank())
                .orElse(fallbackPhone);
    }

    private java.util.Optional<User> loadUser(Long customerId) {
        if (customerId == null) {
            return java.util.Optional.empty();
        }
        return userRepository.findById(customerId);
    }

    private int toIntegerAmount(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.UNNECESSARY).intValueExact();
    }

    private void validateStayPeriod(LocalDateTime checkInAt, LocalDateTime checkOutAt) {
        if (!checkOutAt.isAfter(checkInAt)) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "체크아웃 일시는 체크인 일시보다 이후여야 합니다.");
        }
    }

    private void validateAccommodation(Accommodation accommodation) {
        if (accommodation.getBusinessStatus() != BusinessStatus.OPEN) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "예약 가능한 상태의 숙소가 아닙니다.");
        }
    }

    private void validateRoom(Accommodation accommodation, Room room) {
        if (!room.getAccommodationId().equals(accommodation.getId())) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "해당 숙소에 속한 객실 코드가 아닙니다.");
        }
        if (!Boolean.TRUE.equals(room.getActive())) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "비활성화된 객실입니다.");
        }
    }

    private void validateOccupancy(Room room, int adultCount, int childCount) {
        if (adultCount <= 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "성인 인원은 1명 이상이어야 합니다.");
        }
        if (adultCount + childCount > room.getMaxOccupancy()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 최대 투숙 인원을 초과했습니다.");
        }
    }

    private void validateStayNights(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        long stayNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (stayNights < room.getMinStayNights()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 최소 숙박 일수보다 짧습니다.");
        }
        if (stayNights > room.getMaxStayNights()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "객실 최대 숙박 일수를 초과했습니다.");
        }
    }

    private void validateInventories(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            List<RoomInventory> inventories
    ) {
        int expectedNights = Math.toIntExact(ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        if (inventories.size() != expectedNights) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "선택한 날짜의 재고 정보가 충분하지 않습니다.");
        }
        for (RoomInventory inventory : inventories) {
            if (Boolean.TRUE.equals(inventory.getStopSale())) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "판매 중지된 날짜가 포함되어 있습니다.");
            }
            if (inventory.getAvailableStock() <= 0) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "선택한 날짜에 예약 가능한 재고가 없습니다.");
            }
        }
    }

    private void validateRoomRates(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            List<RoomRate> roomRates
    ) {
        int expectedNights = Math.toIntExact(ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        if (roomRates.size() != expectedNights) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "선택한 날짜의 요금 정보가 충분하지 않습니다.");
        }
    }

    // 최종 청구금액 = 일자별 판매가 합계를 전제 (할인, 추가금 X)
    private void validatePaymentAmount(java.math.BigDecimal paymentAmount, List<RoomRate> roomRates) {
        java.math.BigDecimal expectedAmount = roomRates.stream()
                .map(RoomRate::getSalePrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        if (paymentAmount.compareTo(expectedAmount) != 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "결제 금액이 선택한 날짜의 객실 요금 합계와 일치하지 않습니다.");
        }
    }

    private void validateCancelableReservation(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "확정된 예약만 취소할 수 있습니다.");
        }
        if (!reservation.getCheckInAt().isAfter(LocalDateTime.now())) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "체크인 이후에는 예약을 취소할 수 없습니다.");
        }
    }

    private void validateInventoriesForRestore(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            List<RoomInventory> inventories
    ) {
        int expectedNights = Math.toIntExact(ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        if (inventories.size() != expectedNights) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "취소 대상 날짜의 재고 정보가 충분하지 않습니다.");
        }
        for (RoomInventory inventory : inventories) {
            if (inventory.getReservedStock() <= 0) {
                throw new ApiException(ExceptionType.INVALID_INPUT, "복구할 예약 재고가 존재하지 않습니다.");
            }
        }
    }

    private List<Reservation> loadAccessibleReservations(Long customerId, String guestName, String guestPhoneNumber) {
        if (customerId != null) {
            return reservationRepository.findAllByUserIdOrderByCreatedAtDesc(customerId);
        }

        validateGuestLookupInput(guestName, guestPhoneNumber);
        return reservationRepository.findAllByGuestNameAndGuestPhoneNumberOrderByCreatedAtDesc(guestName, guestPhoneNumber);
    }

    private Reservation findAccessibleReservation(
            Long customerId,
            String guestName,
            String guestPhoneNumber,
            String reservationNo
    ) {
        if (customerId != null) {
            return reservationRepository.findByReservationNoAndUserId(reservationNo, customerId)
                    .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "조회 가능한 예약 내역이 없습니다."));
        }

        validateGuestLookupInput(guestName, guestPhoneNumber);
        return reservationRepository.findByReservationNoAndGuestNameAndGuestPhoneNumber(
                        reservationNo,
                        guestName,
                        guestPhoneNumber
                )
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "조회 가능한 예약 내역이 없습니다."));
    }

    private void validateGuestLookupInput(String guestName, String guestPhoneNumber) {
        if (guestName == null || guestName.isBlank() || guestPhoneNumber == null || guestPhoneNumber.isBlank()) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "비회원 예약 조회 시 투숙객명과 휴대폰 번호는 필수입니다.");
        }
    }

    private Map<Long, Accommodation> getAccommodationMap(List<Reservation> reservations) {
        List<Long> accommodationIds = reservations.stream()
                .map(Reservation::getAccommodationId)
                .distinct()
                .toList();
        Map<Long, Accommodation> accommodationMap = new LinkedHashMap<>();
        accommodationRepository.findAllById(accommodationIds)
                .forEach(accommodation -> accommodationMap.put(accommodation.getId(), accommodation));
        return accommodationMap;
    }

    private boolean isDomesticReservation(Reservation reservation, Map<Long, Accommodation> accommodationMap) {
        Accommodation accommodation = accommodationMap.get(reservation.getAccommodationId());
        return accommodation != null
                && accommodation.getRegionType() == AccommodationRegionType.DOMESTIC;
    }

    private boolean matchesListType(
            Reservation reservation,
            CustomerReservationListType type,
            LocalDate today
    ) {
        return switch (type) {
            case BEFORE_USE -> reservation.getReservationStatus() != ReservationStatus.CANCELLED
                    && !reservation.getCheckOutAt().toLocalDate().isBefore(today);
            case AFTER_USE -> reservation.getReservationStatus() == ReservationStatus.COMPLETED
                    || (reservation.getReservationStatus() == ReservationStatus.CONFIRMED
                    && reservation.getCheckOutAt().toLocalDate().isBefore(today));
            case CANCELLED -> reservation.getReservationStatus() == ReservationStatus.CANCELLED;
        };
    }

    private CustomerReservationPageResponse toPageResponse(
            List<CustomerReservationItemResponse> items,
            Pageable pageable
    ) {
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        long totalElements = items.size();

        if (size <= 0) {
            return CustomerReservationPageResponse.of(
                    items,
                    pageable,
                    totalElements,
                    totalElements == 0 ? 0 : 1,
                    page == 0,
                    true
            );
        }

        int fromIndex = Math.min(page * size, items.size());
        int toIndex = Math.min(fromIndex + size, items.size());
        List<CustomerReservationItemResponse> content = items.subList(fromIndex, toIndex);
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        return CustomerReservationPageResponse.of(
                content,
                pageable,
                totalElements,
                totalPages,
                page == 0,
                totalPages == 0 || page >= totalPages - 1
        );
    }

    private record OndaReservationSyncTarget(
            String propertyId,
            String roomtypeId,
            String rateplanId
    ) {
    }

    private record ReservationRatePlanMapping(
            String roomtypeId,
            String rateplanId
    ) {
    }

    private record GuestCounts(
            int adultCount,
            int childCount
    ) {
    }

    private record ReservationPreparation(
            Accommodation accommodation,
            Room room,
            GuestCounts guestCounts,
            List<RoomRate> roomRates,
            List<RoomInventory> inventories,
            OndaReservationSyncTarget ondaSyncTarget
    ) {
    }
}
