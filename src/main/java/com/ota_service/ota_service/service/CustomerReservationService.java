package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationRequest;
import com.ota_service.ota_service.dto.customer.reservation.CreateCustomerReservationResponse;
import com.ota_service.ota_service.entity.Accommodation;
import com.ota_service.ota_service.entity.Payment;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.entity.RoomInventory;
import com.ota_service.ota_service.entity.RoomRate;
import com.ota_service.ota_service.enums.BusinessStatus;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.AccommodationRepository;
import com.ota_service.ota_service.repository.PaymentRepository;
import com.ota_service.ota_service.repository.ReservationRepository;
import com.ota_service.ota_service.repository.RoomInventoryRepository;
import com.ota_service.ota_service.repository.RoomRateRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import com.ota_service.ota_service.util.CodeGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {

    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final RoomRateRepository roomRateRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public CreateCustomerReservationResponse createReservation(
            Long customerId,
            CreateCustomerReservationRequest request
    ) {
        validateStayPeriod(request.checkInDate(), request.checkOutDate());

        Accommodation accommodation = accommodationRepository.findByCode(request.accommodationCode())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 숙소 코드입니다."));
        validateAccommodation(accommodation);

        Room room = roomRepository.findByRoomCode(request.roomCode())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "존재하지 않는 객실 코드입니다."));
        validateRoom(accommodation, room);

        int adultCount = request.adultCount() == null ? 1 : request.adultCount();
        int childCount = request.childCount() == null ? 0 : request.childCount();
        validateOccupancy(room, adultCount, childCount);
        validateStayNights(room, request.checkInDate(), request.checkOutDate());

        LocalDate stayEndDate = request.checkOutDate().minusDays(1);
        List<RoomRate> roomRates = roomRateRepository.findAllByRoomIdAndActiveTrueAndRateDateBetweenOrderByRateDateAsc(
                room.getId(),
                request.checkInDate(),
                stayEndDate
        );
        validateRoomRates(request.checkInDate(), request.checkOutDate(), roomRates);
        validatePaymentAmount(request.paymentAmount(), roomRates);

        List<RoomInventory> inventories = roomInventoryRepository.findAllByRoomIdAndInventoryDateBetweenAndActiveTrueOrderByInventoryDateAsc(
                room.getId(),
                request.checkInDate(),
                stayEndDate
        );
        validateInventories(request.checkInDate(), request.checkOutDate(), inventories);

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = Reservation.createConfirmed(
                customerId,
                accommodation.getId(),
                room.getId(),
                roomRates.getFirst().getId(),
                "TMP-RSV-" + now.toLocalDate() + "-" + System.nanoTime(),
                request.checkInDate(),
                request.checkOutDate(),
                request.guestName(),
                request.guestPhoneNumber(),
                adultCount,
                childCount,
                request.paymentAmount(),
                now
        );
        reservationRepository.save(reservation);
        reservation.changeReservationNo(codeGenerator.generateReservationCode(LocalDate.now(), reservation.getId()));

        Payment payment = Payment.createPaid(
                reservation.getId(),
                "TMP-PAY-" + now.toLocalDate() + "-" + System.nanoTime(),
                request.paymentMethod(),
                request.paymentAmount(),
                roomRates.getFirst().getCurrency(),
                now
        );
        paymentRepository.save(payment);
        payment.changePaymentNo(codeGenerator.generatePaymentCode(LocalDate.now(), payment.getId()));

        for (RoomInventory inventory : inventories) {
            inventory.setReservedStock(inventory.getReservedStock() + 1);
            inventory.setAvailableStock(inventory.getAvailableStock() - 1);
        }

        return new CreateCustomerReservationResponse(
                reservation.getReservationNo(),
                reservation.getReservationStatus().name(),
                accommodation.getCode(),
                room.getRoomCode(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                payment.getPaymentNo(),
                payment.getPaymentStatus().name(),
                payment.getPaymentMethod(),
                payment.getPaymentAmount(),
                payment.getCurrency(),
                reservation.getConfirmedAt()
        );
    }

    private void validateStayPeriod(LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "체크아웃 날짜는 체크인 날짜보다 이후여야 합니다.");
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

    private void validatePaymentAmount(java.math.BigDecimal paymentAmount, List<RoomRate> roomRates) {
        java.math.BigDecimal expectedAmount = roomRates.stream()
                .map(RoomRate::getSalePrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        if (paymentAmount.compareTo(expectedAmount) != 0) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "결제 금액이 선택한 날짜의 객실 요금 합계와 일치하지 않습니다.");
        }
    }
}
