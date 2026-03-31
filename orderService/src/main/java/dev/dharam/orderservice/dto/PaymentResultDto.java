package dev.dharam.orderservice.dto;

public record PaymentResultDto(
        boolean isSuccess,
        String message,
        String transactionId
) {
}
