package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.supplier.onda.OndaCancelReservationRequest;
import com.ota_service.ota_service.dto.supplier.onda.OndaCancelReservationResponse;
import com.ota_service.ota_service.dto.supplier.onda.OndaCheckAvailabilityResponse;
import com.ota_service.ota_service.dto.supplier.onda.OndaCreateReservationRequest;
import com.ota_service.ota_service.dto.supplier.onda.OndaCreateReservationResponse;
import java.time.LocalDate;

public interface OndaSupplierReservationClient {

    OndaCheckAvailabilityResponse checkAvailability(
            String propertyId,
            String roomtypeId,
            String rateplanId,
            LocalDate checkIn,
            LocalDate checkOut
    );

    OndaCreateReservationResponse createReservation(String propertyId, OndaCreateReservationRequest request);

    OndaCancelReservationResponse cancelReservation(
            String propertyId,
            String bookingNumber,
            OndaCancelReservationRequest request
    );
}
