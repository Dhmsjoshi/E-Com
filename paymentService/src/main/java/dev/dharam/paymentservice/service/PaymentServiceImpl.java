package dev.dharam.paymentservice.service;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import dev.dharam.paymentservice.adapter.PaymentGatewayAdepter;
import dev.dharam.paymentservice.client.orderservice.OrderServiceClient;
import dev.dharam.paymentservice.model.Payment;
import dev.dharam.paymentservice.model.PaymentProvider;
import dev.dharam.paymentservice.model.PaymentStatus;
import dev.dharam.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
    @Value("${razorpay.webhook.secret}")
    private String secret;

    private final PaymentGatewayAdepter paymentGatewayAdepter;
    private final OrderServiceClient orderServiceClient;
    private final PaymentRepository  paymentRepository;

    @Override
    public String createPaymentLink(Long orderId, String email, String phoneNumber) {

        log.info("Payment process started for Order ID: {}",orderId);

        //Fetch the amount from Order Service client
        Long amount = orderServiceClient.getOrderAmount(orderId);
        log.info("Amount received from Order Servie: {} for Order: {}",amount,orderId);

        //Call to Razorpay Adapter to generate link
//        String paymentLink = paymentGatewayAdepter.createPaymentLink(orderId,amount,email,phoneNumber);
//        log.info("Payment link generated successfully: {}", paymentLink);

        Map<String, String> gatewayData =  paymentGatewayAdepter.createPaymentLink(orderId,amount,email,phoneNumber);
        String paymentUrl = gatewayData.get("payment_link_url");
        String  paymentLinkId = gatewayData.get("payment_link_id");

        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .gatewayName(PaymentProvider.RAZORPAY.toString())
                .externalOrderId(paymentLinkId) // payment_link_id
                .paymentLinkUrl(paymentUrl) // generated payment url
                .idempotencyKey(UUID.randomUUID().toString()) // for safety
                .externalReferenceId(String.valueOf(orderId))

                .build();

        paymentRepository.save(payment);

        return paymentUrl;

    }

    @Override
    public void processWebhook(String payload, String signature) {
        //Signature varification
        boolean isValid = verifyWebhookSignature(payload, signature, secret);

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        // 2. Check if payment is successful
        if ("payment_link.paid".equals(event)) {
            JSONObject paymentLinkObj = json.getJSONObject("payload")
                    .getJSONObject("payment_link")
                    .getJSONObject("entity");

            String paymentLinkId = paymentLinkObj.getString("id"); // Ye wahi 'plink_xxx' hai
            String paymentId = json.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity")
                    .getString("id"); // Ye 'pay_xxx' hai

            // Fetch Database  PENDING record
            Payment payment = paymentRepository.findByExternalOrderId(paymentLinkId)
                    .orElseThrow(() -> new RuntimeException("Payment record not found for: " + paymentLinkId));

            // 4. Status Update
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(paymentId); // Final Transaction ID save kar li
            payment.setRawWebhookData(payload);  // Backup ke liye pura data save kar liya
            paymentRepository.save(payment);

            // 5. Order Service ko call karo (Feign/RestTemplate se)
            orderServiceClient.updatePaymentStatus(payment.getOrderId(),true,payment.getTransactionId());
            log.info("Order #{} marked as PAID successfully!", payment.getOrderId());
        }
    }

        private boolean verifyWebhookSignature (String payload, String signature, String secret){
            try {
                return Utils.verifyWebhookSignature(payload, signature, secret);
            } catch (RazorpayException e) {
                return false;
            }
        }



}
