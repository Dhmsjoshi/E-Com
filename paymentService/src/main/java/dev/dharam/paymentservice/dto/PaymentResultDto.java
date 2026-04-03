package dev.dharam.paymentservice.dto;

public record PaymentResultDto(
        boolean isSuccess,
        String message,
        String transactionId
) {
}
