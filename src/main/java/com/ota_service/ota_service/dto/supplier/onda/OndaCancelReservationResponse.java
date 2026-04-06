package com.ota_service.ota_service.dto.supplier.onda;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OndaCancelReservationResponse(
        String bookingNumber,
        String channelBookingNumber,
        String currency,
        Integer totalAmount,
        Integer refundAmount
) {
    public static OndaCancelReservationResponse of(
            String bookingNumber,
            String channelBookingNumber,
            String currency,
            Integer totalAmount,
            Integer refundAmount
    ) {
        return OndaCancelReservationResponse.builder()
                .bookingNumber(bookingNumber)
                .channelBookingNumber(channelBookingNumber)
                .currency(currency)
                .totalAmount(totalAmount)
                .refundAmount(refundAmount)
                .build();
    }
}
