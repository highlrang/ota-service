package com.ota_service.ota_service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {

    private static final DateTimeFormatter RESERVATION_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    public String generateTemporaryAccommodationCode() {
        return "TMP-" + UUID.randomUUID();
    }

    public String generateAccommodationCode(Long accommodationId) {
        return "ACC-" + String.format("%06d", accommodationId);
    }

    public String generateTemporaryRoomCode() {
        return "TMP-ROOM-" + UUID.randomUUID();
    }

    public String generateRoomCode(Long roomId) {
        return "ROOM-" + String.format("%06d", roomId);
    }

    public String generateReservationCode(LocalDate reservationDate, Long sequence) {
        return "RSV-" + reservationDate.format(RESERVATION_DATE_FORMAT) + "-" + String.format("%04d", sequence);
    }

    public String generatePaymentCode(LocalDate paymentDate, Long sequence) {
        return "PAY-" + paymentDate.format(RESERVATION_DATE_FORMAT) + "-" + String.format("%04d", sequence);
    }
}
