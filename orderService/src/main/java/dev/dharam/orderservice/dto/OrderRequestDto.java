package dev.dharam.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDto(
        @NotEmpty(message = "Order must have at least one item")
        @Valid // CRITICAL: Ye andar ke OrderItemRequestDto ko validate karne ke liye zaroori hai
        List<OrderItemRequestDto> items
) {
}
