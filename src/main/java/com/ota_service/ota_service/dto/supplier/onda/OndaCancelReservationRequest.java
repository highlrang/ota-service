package com.ota_service.ota_service.dto.supplier.onda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ota_service.ota_service.dto.customer.reservation.CancelCustomerReservationRequest;
import com.ota_service.ota_service.entity.Payment;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OndaCancelReservationRequest(
        @JsonProperty("canceled_by")
        String canceledBy,
        @JsonProperty("reason")
        String reason,
        @JsonProperty("currency")
        String currency,
        @JsonProperty("total_amount")
        Integer totalAmount,
        @JsonProperty("refund_amount")
        Integer refundAmount
) {
    public static OndaCancelReservationRequest forUserCancel(
            CancelCustomerReservationRequest request,
            Payment payment,
            int refundAmount
    ) {
        return OndaCancelReservationRequest.builder()
                .canceledBy("user")
                .reason(request == null || request.reason() == null || request.reason().isBlank()
                        ? "고객 요청 취소"
                        : request.reason())
                .currency(payment.getCurrency())
                .totalAmount(payment.getPaymentAmount().intValueExact())
                .refundAmount(refundAmount)
                .build();
    }

    public static OndaCancelReservationRequest forSystemRollback(
            String currency,
            int totalAmount,
            String reason
    ) {
        return OndaCancelReservationRequest.builder()
                .canceledBy("system")
                .reason(reason)
                .currency(currency)
                .totalAmount(totalAmount)
                .refundAmount(0)
                .build();
    }
}
