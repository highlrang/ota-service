package com.ota_service.ota_service.dto.supplier.onda;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OndaCheckAvailabilityResponse(
        LocalDate checkin,
        LocalDate checkout,
        String propertyId,
        String roomtypeId,
        String rateplanId,
        boolean availability,
        List<OndaCheckAvailabilityDate> dates
) {
    public static OndaCheckAvailabilityResponse of(
            LocalDate checkin,
            LocalDate checkout,
            String propertyId,
            String roomtypeId,
            String rateplanId,
            boolean availability,
            List<OndaCheckAvailabilityDate> dates
    ) {
        return OndaCheckAvailabilityResponse.builder()
                .checkin(checkin)
                .checkout(checkout)
                .propertyId(propertyId)
                .roomtypeId(roomtypeId)
                .rateplanId(rateplanId)
                .availability(availability)
                .dates(dates)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record OndaCheckAvailabilityDate(
            LocalDate date,
            Integer vacancy
    ) {
        public static OndaCheckAvailabilityDate of(LocalDate date, Integer vacancy) {
            return OndaCheckAvailabilityDate.builder()
                    .date(date)
                    .vacancy(vacancy)
                    .build();
        }
    }
}
