package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNo(String reservationNo);
}
