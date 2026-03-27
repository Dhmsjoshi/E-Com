package dev.dharam.cartservice.dto;

import java.util.UUID;

public record AddToCartRequestDto(
        UUID userId,
        Long productId,
        Integer quantity

        ) {};
