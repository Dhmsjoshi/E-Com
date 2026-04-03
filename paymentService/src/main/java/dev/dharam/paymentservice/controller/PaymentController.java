package dev.dharam.paymentservice.controller;

import dev.dharam.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<String> initiatePayment(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal Jwt jwt
            ){

        String email = jwt.getClaimAsString("email");
        String phoneNumber =jwt.getClaimAsString("phoneNumber");

        String paymentLink = paymentService.createPaymentLink(orderId,email,phoneNumber);
        return ResponseEntity.ok(paymentLink);
    }


}

