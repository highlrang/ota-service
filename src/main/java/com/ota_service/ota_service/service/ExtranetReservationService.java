package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.extranet.reservation.ExtranetReservationItemResponse;
import com.ota_service.ota_service.dto.extranet.reservation.ExtranetReservationPageResponse;
import com.ota_service.ota_service.entity.ExtranetAccommodation;
import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.entity.Room;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.ExtranetAccommodationRepository;
import com.ota_service.ota_service.repository.ReservationRepository;
import com.ota_service.ota_service.repository.RoomRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtranetReservationService {

    private final ExtranetAccommodationRepository extranetAccommodationRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public ExtranetReservationPageResponse getAccommodationReservations(
            Long extranetId,
            String accommodationCode,
            Pageable pageable
    ) {
        ExtranetAccommodation extranetAccommodation = extranetAccommodationRepository.findByExtranetIdAndCode(extranetId, accommodationCode)
                .orElseThrow(() -> new ApiException(ExceptionType.ACCESS_DENIED, "해당 숙소의 예약을 조회할 권한이 없습니다."));

        List<Reservation> reservations = reservationRepository.findAllByAccommodationIdOrderByCreatedAtDesc(
                extranetAccommodation.getAccommodationId()
        );
        if (reservations.isEmpty()) {
            return ExtranetReservationPageResponse.empty(pageable);
        }

        Map<Long, Room> roomMap = roomRepository.findAllByAccommodationIdOrderByIdAsc(extranetAccommodation.getAccommodationId())
                .stream()
                .collect(Collectors.toMap(Room::getId, Function.identity()));

        List<ExtranetReservationItemResponse> items = reservations.stream()
                .map(reservation -> ExtranetReservationItemResponse.of(
                        reservation,
                        resolveRoom(roomMap, reservation)
                ))
                .toList();

        return toPageResponse(items, pageable);
    }

    private Room resolveRoom(Map<Long, Room> roomMap, Reservation reservation) {
        Room room = roomMap.get(reservation.getRoomId());
        if (room == null) {
            throw new ApiException(ExceptionType.INVALID_INPUT, "예약에 연결된 객실 정보가 존재하지 않습니다.");
        }
        return room;
    }

    private ExtranetReservationPageResponse toPageResponse(
            List<ExtranetReservationItemResponse> items,
            Pageable pageable
    ) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int totalElements = items.size();
        int fromIndex = page * size;

        if (fromIndex >= totalElements) {
            int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
            return ExtranetReservationPageResponse.of(
                    List.of(),
                    pageable,
                    totalElements,
                    totalPages,
                    page == 0,
                    true
            );
        }

        int toIndex = Math.min(fromIndex + size, totalElements);
        List<ExtranetReservationItemResponse> content = items.subList(fromIndex, toIndex);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return ExtranetReservationPageResponse.of(
                content,
                pageable,
                totalElements,
                totalPages,
                page == 0,
                toIndex >= totalElements
        );
    }
}
