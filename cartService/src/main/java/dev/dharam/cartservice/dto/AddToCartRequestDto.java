package dev.dharam.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request object to add an item to the cart")
public record AddToCartRequestDto(

        @Schema(description = "ID of the product to be added", example = "101")
        @NotNull(message = "Product ID cannot be null")
        Long productId,

        @Schema(description = "Quantity of the product", example = "2")
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity

        ) {};
