package com.ota_service.ota_service.entity;

import com.ota_service.ota_service.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RESERVATIONS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "room_rate_id", nullable = false)
    private Long roomRateId;

    @Column(name = "reservation_no", nullable = false, unique = true, length = 100)
    private String reservationNo;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "guest_name", nullable = false, length = 100)
    private String guestName;

    @Column(name = "guest_phone_number", nullable = false, length = 30)
    private String guestPhoneNumber;

    @Column(name = "adult_count", nullable = false)
    private Integer adultCount;

    @Column(name = "child_count", nullable = false)
    private Integer childCount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus reservationStatus;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    public static Reservation createConfirmed(
            Long userId,
            Long accommodationId,
            Long roomId,
            Long roomRateId,
            String reservationNo,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            String guestName,
            String guestPhoneNumber,
            Integer adultCount,
            Integer childCount,
            BigDecimal totalAmount,
            LocalDateTime confirmedAt
    ) {
        Reservation reservation = new Reservation();
        reservation.userId = userId;
        reservation.accommodationId = accommodationId;
        reservation.roomId = roomId;
        reservation.roomRateId = roomRateId;
        reservation.reservationNo = reservationNo;
        reservation.checkInDate = checkInDate;
        reservation.checkOutDate = checkOutDate;
        reservation.guestName = guestName;
        reservation.guestPhoneNumber = guestPhoneNumber;
        reservation.adultCount = adultCount;
        reservation.childCount = childCount;
        reservation.totalAmount = totalAmount;
        reservation.reservationStatus = ReservationStatus.CONFIRMED;
        reservation.requestedAt = confirmedAt;
        reservation.confirmedAt = confirmedAt;
        return reservation;
    }

    public void changeReservationNo(String reservationNo) {
        this.reservationNo = reservationNo;
    }
}
