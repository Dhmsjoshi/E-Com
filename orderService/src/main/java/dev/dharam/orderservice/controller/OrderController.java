package dev.dharam.orderservice.controller;

import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.dto.PaymentResultDto;
import dev.dharam.orderservice.dto.UpdateOrdeStatusRequestDto;
import dev.dharam.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> placeOrder(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody OrderRequestDto requestDto) {

        UUID dummyUserId = extractUserId(jwt);

        OrderResponseDto response = orderService.createOrder(dummyUserId, requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get order details", description = "Fetch full details of an order including its items and current status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @Operation(summary = "Get current user's orders", description = "Retrieves a list of all historical orders for the logged-in user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@AuthenticationPrincipal Jwt jwt) {

        UUID dummyUserId = extractUserId(jwt);

        return ResponseEntity.ok(orderService.getOrdersByUserId(dummyUserId));
    }

    @Operation(summary = "Cancel an active order", description = "Cancels an order if it's not yet delivered. Updates both order and payment status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to cancel this order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> cancelOrder(@AuthenticationPrincipal Jwt jwt,@PathVariable Long orderId) {

        UUID dummyUserId = extractUserId(jwt);

        return ResponseEntity.ok(orderService.cancelOrder(orderId, dummyUserId));
    }

    // PAYMENT CALLBACK
    @Operation(
            summary = "Update Payment Result",
            description = "Automated callback for payment success/failure. Restores stock if payment fails."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{orderId}/payment-status")
    public ResponseEntity<Void> updatePaymentResult(@PathVariable("orderId") Long orderId,
                                                    @RequestBody PaymentResultDto paymentResult){

        if(paymentResult.isSuccess()){
            orderService.markAsPaid(orderId);
        }else{
            orderService.markAsCancelled(orderId);
        }
        return ResponseEntity.ok().build();
    }


    //  ADMIN STATUS UPDATE
    @Operation(
            summary = "Update Order Status (Admin Only)",
            description = "Allows admin to change status to SHIPPED, DELIVERED, or RETURNED. Restores stock if RETURNED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access Denied - Admin role required")
    })
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable("orderId") Long orderId,
                                                         @Valid @RequestBody UpdateOrdeStatusRequestDto requestDto){

        return ResponseEntity.ok(orderService.updateOrderStatus(orderId,requestDto.status()));
    }


    @Operation(
            summary = "Get Order Amount for Payment",
            description = "Fetches the total order amount in the smallest currency unit (Paise) for the Payment Service. Only allowed for PENDING orders."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Order Status (Not PENDING)"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}/payment-amount")
    public ResponseEntity<Long> getPaymentAmount(@PathVariable("orderId") Long orderId){
        return ResponseEntity.ok(orderService.getOrderAmount(orderId));
    }


    private UUID extractUserId(Jwt jwt) {
        String userIdStr = jwt.getClaimAsString("user_id");

        if (userIdStr == null || userIdStr.isBlank()) {
            throw new IllegalArgumentException("User ID missing or invalid in the security token!");
        }

        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format in token!");
        }
    }

}
