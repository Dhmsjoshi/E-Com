package dev.dharam.paymentservice.adapter;

import dev.dharam.paymentservice.model.PaymentProvider;

public interface PaymentGatewayAdepter {

    // to generate payment link
    String createPaymentLink(Long orderId, Long amount,String email,String phoneNumber);

    //to give gateway name
    PaymentProvider getProviderName();

}
