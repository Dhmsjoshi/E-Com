package dev.dharam.cartservice.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record CartResponseDto(
        UUID userId,
        List<CartItemResponseDto> items,
        Double totalAmount
)implements Serializable {
}
