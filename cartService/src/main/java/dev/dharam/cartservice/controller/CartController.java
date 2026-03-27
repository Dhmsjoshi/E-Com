package dev.dharam.cartservice.controller;

import dev.dharam.cartservice.dto.AddToCartRequestDto;
import dev.dharam.cartservice.dto.CartResponseDto;
import dev.dharam.cartservice.service.cartservice.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody AddToCartRequestDto requestDto) {
        return ResponseEntity.ok().body(cartService.addToCart(requestDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok().body(cartService.getCart(userId));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartResponseDto> removeItemFromCart(@PathVariable("userId") UUID userId,
                                                              @PathVariable("productId") Long productId) {
        return ResponseEntity.ok().body(cartService.removeItemFromCart(userId, productId));
    }

}
