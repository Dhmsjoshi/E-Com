package dev.dharam.paymentservice.adapter.razorpayAdapter;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import dev.dharam.paymentservice.adapter.PaymentGatewayAdepter;
import dev.dharam.paymentservice.model.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class RazorpayAdapter implements PaymentGatewayAdepter {
    private final RazorpayClient razorpayClient;

    @Override
    public String createPaymentLink(Long orderId, Long amount, String email,String phoneNumber) {
        try{
            //prepare request body (JSON format)
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("accept_partial", false);

            //set expiry after 15 min
            paymentLinkRequest.put("expire_by", Instant.now().getEpochSecond()+900);
            paymentLinkRequest.put("reference_id", orderId.toString());
            paymentLinkRequest.put("description", "Payment for Order #"+orderId);

            //customer info
            JSONObject customer = new JSONObject();
            customer.put("contact", phoneNumber);
            customer.put("email", email);
            customer.put("name", "Dharmesh Joshi");

            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            //callbacks(after payment, it redirects)
            paymentLinkRequest.put("callback_url", "http://localhost:8081/api/v1/orders/confirm");
            paymentLinkRequest.put("callback_method", "get");

            //generate link from Razorpay SDK
            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            //Short_url to show to users
            return paymentLink.get("short_url").toString();


        }catch (RazorpayException e){
            log.error("Razorpay error for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Razorpay payment initiation failed!");
        }

    }

    @Override
    public PaymentProvider getProviderName() {
        return PaymentProvider.RAZORPAY;
    }
}
