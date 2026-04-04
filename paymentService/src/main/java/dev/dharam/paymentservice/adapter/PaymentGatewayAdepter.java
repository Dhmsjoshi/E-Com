package dev.dharam.paymentservice.adapter;

import dev.dharam.paymentservice.model.PaymentProvider;
import dev.dharam.paymentservice.model.PaymentStatus;

import java.util.Map;

public interface PaymentGatewayAdepter {

    // to generate payment link
    Map<String,String> createPaymentLink(Long orderId, Long amount, String email, String phoneNumber);

    //for reconciliation
    PaymentStatus getStatus(String externalOrderId);
    //to give gateway name
    PaymentProvider getProviderName();

}
