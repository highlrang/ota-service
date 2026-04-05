package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.customer.reservation.PaymentApprovalRequest;
import com.ota_service.ota_service.dto.customer.reservation.PaymentApprovalResponse;
import com.ota_service.ota_service.dto.customer.reservation.PaymentRefundRequest;
import com.ota_service.ota_service.dto.customer.reservation.PaymentRefundResponse;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockPaymentGatewayClient implements PaymentGatewayClient {

    @Override
    public PaymentApprovalResponse approve(PaymentApprovalRequest request) {
        log.info(
                "Mock payment approval success reservationNo={} paymentMethod={} paymentAmount={} currency={}",
                request.reservationNo(),
                request.paymentMethod(),
                request.paymentAmount(),
                request.currency()
        );
        return PaymentApprovalResponse.approved(LocalDateTime.now());
    }

    @Override
    public PaymentRefundResponse refund(PaymentRefundRequest request) {
        log.info(
                "Mock payment refund success reservationNo={} paymentNo={} refundAmount={} reason={}",
                request.reservationNo(),
                request.paymentNo(),
                request.refundAmount(),
                request.reason()
        );
        return PaymentRefundResponse.refunded(LocalDateTime.now());
    }
}
