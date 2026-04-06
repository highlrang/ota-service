package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNo(String reservationNo);

    Optional<Reservation> findByReservationNoAndUserId(String reservationNo, Long userId);

    Optional<Reservation> findByReservationNoAndGuestNameAndGuestPhoneNumber(
            String reservationNo,
            String guestName,
            String guestPhoneNumber
    );

    List<Reservation> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<Reservation> findAllByGuestNameAndGuestPhoneNumberOrderByCreatedAtDesc(
            String guestName,
            String guestPhoneNumber
    );

    List<Reservation> findAllByAccommodationIdOrderByCreatedAtDesc(Long accommodationId);

    List<Reservation> findAllByReservationStatusAndCheckOutAtBefore(
            ReservationStatus reservationStatus,
            LocalDateTime checkOutAt
    );

    boolean existsByRoomRateIdIn(Collection<Long> roomRateIds);
}
