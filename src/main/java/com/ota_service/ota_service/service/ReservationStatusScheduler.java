package com.ota_service.ota_service.service;

import com.ota_service.ota_service.entity.Reservation;
import com.ota_service.ota_service.enums.ReservationStatus;
import com.ota_service.ota_service.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;

    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void completeFinishedReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findAllByReservationStatusAndCheckOutAtBefore(
                ReservationStatus.CONFIRMED,
                now
        );

        for (Reservation reservation : reservations) {
            reservation.setReservationStatus(ReservationStatus.COMPLETED);
        }

        if (!reservations.isEmpty()) {
            log.info("Marked {} reservations as COMPLETED", reservations.size());
        }
    }
}
