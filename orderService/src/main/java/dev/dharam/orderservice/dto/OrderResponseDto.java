package dev.dharam.orderservice.dto;

import dev.dharam.orderservice.model.OrderStatus;
import dev.dharam.orderservice.model.PaymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        Long id,
        UUID userId,
        Double totalAmount,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        Instant createdAt,
        List<OrderItemResponseDto> items

) {};
