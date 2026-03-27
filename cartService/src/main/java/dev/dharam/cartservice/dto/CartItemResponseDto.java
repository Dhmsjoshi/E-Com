package dev.dharam.cartservice.dto;

import java.io.Serializable;

public record CartItemResponseDto(
        Long id,
        Long productId,
        String productName,
        Double price,
        Integer quantity,
        Double itemTotal
) implements Serializable {};
