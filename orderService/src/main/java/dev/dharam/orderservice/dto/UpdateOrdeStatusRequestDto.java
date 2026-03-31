package dev.dharam.orderservice.dto;

import dev.dharam.orderservice.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrdeStatusRequestDto(
        @NotNull(message = "Status cannot be null")
        OrderStatus status
) {
}
