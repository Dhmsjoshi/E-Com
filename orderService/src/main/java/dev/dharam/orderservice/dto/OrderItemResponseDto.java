package dev.dharam.orderservice.dto;

public record OrderItemResponseDto(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        Double price
) {};
