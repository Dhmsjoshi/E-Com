package dev.dharam.cartservice.service.cartservice;

import dev.dharam.cartservice.dto.AddToCartRequestDto;
import dev.dharam.cartservice.dto.CartResponseDto;
import dev.dharam.cartservice.model.Cart;

import java.util.UUID;

public interface CartService {
    CartResponseDto addToCart(UUID userId, AddToCartRequestDto requestDto);
    CartResponseDto getCart(UUID userId);
    CartResponseDto removeItemFromCart(UUID userId, Long productId);
}
