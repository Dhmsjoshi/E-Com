package dev.dharam.orderservice.controller;

import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Order Management", description = "Endpoints for creating, managing, and tracking customer orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place a new order",
            description = "Creates a new order for the authenticated user. Validates item availability and sets initial PENDING status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or empty cart")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        // later we will get it from token
        UUID dummyUserId = UUID.fromString("11111111-2222-3333-4444-555555555555");

        OrderResponseDto response = orderService.createOrder(dummyUserId, requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get order details", description = "Fetch full details of an order including its items and current status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @Operation(summary = "Get current user's orders", description = "Retrieves a list of all historical orders for the logged-in user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders() {
        // leter take it from token
        UUID dummyUserId = UUID.fromString("11111111-2222-3333-4444-555555555555");

        return ResponseEntity.ok(orderService.getOrdersByUserId(dummyUserId));
    }

    @Operation(summary = "Cancel an active order", description = "Cancels an order if it's not yet delivered. Updates both order and payment status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to cancel this order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long orderId) {
        // leter take it from token
        UUID dummyUserId = UUID.fromString("11111111-2222-3333-4444-555555555555");

        return ResponseEntity.ok(orderService.cancelOrder(orderId, dummyUserId));
    }

}
