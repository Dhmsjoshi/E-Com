package dev.dharam.paymentservice.adapter;

import dev.dharam.paymentservice.model.PaymentProvider;

import java.util.Map;

public interface PaymentGatewayAdepter {

    // to generate payment link
    Map<String,String> createPaymentLink(Long orderId, Long amount, String email, String phoneNumber);

    //to give gateway name
    PaymentProvider getProviderName();

}
