package com.ota_service.ota_service.dto.supplier.onda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ota_service.ota_service.entity.Reservation;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OndaCreateReservationRequest(
        @JsonProperty("currency")
        String currency,
        @JsonProperty("channel_booking_number")
        String channelBookingNumber,
        @JsonProperty("checkin")
        LocalDate checkin,
        @JsonProperty("checkout")
        LocalDate checkout,
        @JsonProperty("rateplans")
        List<RateplanReservation> rateplans,
        @JsonProperty("booker")
        Booker booker
) {
    public static OndaCreateReservationRequest forReservation(
            Reservation reservation,
            String currency,
            String roomtypeId,
            String rateplanId,
            Integer amount,
            String bookerName,
            String bookerEmail,
            String bookerPhone,
            String nationality,
            String timezone
    ) {
        return OndaCreateReservationRequest.builder()
                .currency(currency)
                .channelBookingNumber(reservation.getReservationNo())
                .checkin(reservation.getCheckInAt().toLocalDate())
                .checkout(reservation.getCheckOutAt().toLocalDate())
                .rateplans(List.of(RateplanReservation.of(
                        roomtypeId,
                        rateplanId,
                        amount,
                        reservation.getAdultCount(),
                        reservation.getChildCount(),
                        reservation.getGuestName(),
                        bookerEmail,
                        reservation.getGuestPhoneNumber(),
                        nationality
                )))
                .booker(Booker.of(
                        bookerName,
                        bookerEmail,
                        bookerPhone,
                        nationality,
                        timezone
                ))
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record RateplanReservation(
            @JsonProperty("roomtype_id")
            String roomtypeId,
            @JsonProperty("rateplan_id")
            String rateplanId,
            @JsonProperty("amount")
            Integer amount,
            @JsonProperty("number_of_guest")
            GuestCount numberOfGuest,
            @JsonProperty("guests")
            List<Guest> guests
    ) {
        public static RateplanReservation of(
                String roomtypeId,
                String rateplanId,
                Integer amount,
                Integer adultCount,
                Integer childCount,
                String guestName,
                String guestEmail,
                String guestPhone,
                String nationality
        ) {
            return RateplanReservation.builder()
                    .roomtypeId(roomtypeId)
                    .rateplanId(rateplanId)
                    .amount(amount)
                    .numberOfGuest(GuestCount.of(adultCount, childCount))
                    .guests(List.of(Guest.of(guestName, guestEmail, guestPhone, nationality)))
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record GuestCount(
            @JsonProperty("adult")
            Integer adult,
            @JsonProperty("child")
            Integer child
    ) {
        public static GuestCount of(Integer adult, Integer child) {
            return GuestCount.builder()
                    .adult(adult)
                    .child(child)
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record Guest(
            @JsonProperty("name")
            String name,
            @JsonProperty("email")
            String email,
            @JsonProperty("phone")
            String phone,
            @JsonProperty("nationality")
            String nationality
    ) {
        public static Guest of(String name, String email, String phone, String nationality) {
            return Guest.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .nationality(nationality)
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record Booker(
            @JsonProperty("name")
            String name,
            @JsonProperty("email")
            String email,
            @JsonProperty("phone")
            String phone,
            @JsonProperty("nationality")
            String nationality,
            @JsonProperty("timezone")
            String timezone
    ) {
        public static Booker of(
                String name,
                String email,
                String phone,
                String nationality,
                String timezone
        ) {
            return Booker.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .nationality(nationality)
                    .timezone(timezone)
                    .build();
        }
    }
}
