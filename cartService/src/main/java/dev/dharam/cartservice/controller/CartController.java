package dev.dharam.cartservice.controller;

import dev.dharam.cartservice.dto.AddToCartRequestDto;
import dev.dharam.cartservice.dto.CartResponseDto;
import dev.dharam.cartservice.service.cartservice.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Validated // Method level validation ke liye
@Tag(name = "Cart Management", description = "Endpoints for managing user shopping carts")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart. If cart doesn't exist, it creates one.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT required")
    })
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CartResponseDto> addToCart(@Valid @RequestBody AddToCartRequestDto requestDto,
                                                     @AuthenticationPrincipal Jwt jwt) {

        UUID userId = extractUserId(jwt);

        return ResponseEntity.ok().body(cartService.addToCart(userId,requestDto));
    }

    @Operation(summary = "Get user cart", description = "Retrieves the current shopping cart for the authenticated user.")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = extractUserId(jwt);
        return ResponseEntity.ok().body(cartService.getCart(userId));
    }

    @Operation(summary = "Remove item from cart", description = "Removes a specific product from the authenticated user's cart.")
    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CartResponseDto> removeItemFromCart(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable("productId") Long productId) {
        UUID userId = extractUserId(jwt);
        return ResponseEntity.ok().body(cartService.removeItemFromCart(userId, productId));
    }

    private UUID extractUserId(Jwt jwt) {
        String userIdStr = jwt.getClaimAsString("user_id");

        // Yahan humne exception throw kiya
        if (userIdStr == null || userIdStr.isBlank()) {
            throw new IllegalArgumentException("User ID missing or invalid in the security token!");
        }

        return UUID.fromString(userIdStr);
    }

}
