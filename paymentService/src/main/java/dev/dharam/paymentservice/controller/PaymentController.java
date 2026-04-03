package dev.dharam.paymentservice.controller;

import dev.dharam.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<String> initiatePayment(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal Jwt jwt
            ){

        String email = jwt.getClaimAsString("email");
        String phoneNumber =jwt.getClaimAsString("phone_number");

        String paymentLink = paymentService.createPaymentLink(orderId,email,phoneNumber);
        return ResponseEntity.ok(paymentLink);
    }

//    String ngrokBaseUrl = "https://a1b2-c3d4-e5f6.ngrok-free.app" + /api/v1/payments/webhook
    @PostMapping("/webhook")
    public  ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature
    ){

        log.info("Webhook received!");
        paymentService.processWebhook(payload,signature);
        return ResponseEntity.ok("OK");
    }


}

