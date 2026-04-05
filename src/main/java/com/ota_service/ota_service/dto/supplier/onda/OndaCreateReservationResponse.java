package com.ota_service.ota_service.dto.supplier.onda;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OndaCreateReservationResponse(
        String propertyId,
        String propertyName,
        String bookingNumber,
        String channelBookingNumber,
        String status
) {
    public static OndaCreateReservationResponse of(
            String propertyId,
            String propertyName,
            String bookingNumber,
            String channelBookingNumber,
            String status
    ) {
        return OndaCreateReservationResponse.builder()
                .propertyId(propertyId)
                .propertyName(propertyName)
                .bookingNumber(bookingNumber)
                .channelBookingNumber(channelBookingNumber)
                .status(status)
                .build();
    }
}
