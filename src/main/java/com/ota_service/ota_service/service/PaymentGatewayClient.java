package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.customer.reservation.PaymentApprovalRequest;
import com.ota_service.ota_service.dto.customer.reservation.PaymentApprovalResponse;
import com.ota_service.ota_service.dto.customer.reservation.PaymentRefundRequest;
import com.ota_service.ota_service.dto.customer.reservation.PaymentRefundResponse;

public interface PaymentGatewayClient {
    PaymentApprovalResponse approve(PaymentApprovalRequest request);

    PaymentRefundResponse refund(PaymentRefundRequest request);
}
