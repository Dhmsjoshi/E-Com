package dev.dharam.paymentservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/payments")
public class PaymentUIController {
    @GetMapping("/success-page")
    public String showSuccessPage(@RequestParam("razorpay_payment_id") String paymentId, Model model) {
        // HTML page ko data bhej rahe hain
        model.addAttribute("paymentId", paymentId);
        return "payment-success";
    }
}
