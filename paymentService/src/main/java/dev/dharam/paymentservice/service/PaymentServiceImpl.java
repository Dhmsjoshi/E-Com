package dev.dharam.paymentservice.service;

import dev.dharam.paymentservice.adapter.PaymentGatewayAdepter;
import dev.dharam.paymentservice.client.orderservice.OrderServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{

    private final PaymentGatewayAdepter paymentGatewayAdepter;
    private final OrderServiceClient orderServiceClient;

    @Override
    public String createPaymentLink(Long orderId, String email, String phoneNumber) {

        log.info("Payment process started for Order ID: {}",orderId);

        //Fetch the amount from Order Service client
        Long amount = orderServiceClient.getOrderAmount(orderId);
        log.info("Amount received from Order Servie: {} for Order: {}",amount,orderId);

        //Call to Razorpay Adapter to generate link
        String paymentLink = paymentGatewayAdepter.createPaymentLink(orderId,amount,email,phoneNumber);
        log.info("Payment link generated successfully: {}", paymentLink);

        return paymentLink;

    }
}
