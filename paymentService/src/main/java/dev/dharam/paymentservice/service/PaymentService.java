package dev.dharam.paymentservice.service;

public interface PaymentService {

    String createPaymentLink(Long orderId,String email,String phoneNumber);
    void processWebhook(String payload, String signature);
}
