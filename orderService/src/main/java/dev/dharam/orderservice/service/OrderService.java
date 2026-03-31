package dev.dharam.orderservice.service;

import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.model.Order;
import dev.dharam.orderservice.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponseDto createOrder(UUID userId, OrderRequestDto requestDto);
    OrderResponseDto getOrderDetails(Long orderId);
    OrderResponseDto cancelOrder(Long orderId, UUID userId);
    List<OrderResponseDto> getOrdersByUserId(UUID userId);
    void markAsPaid(Long orderId);
    void markAsCancelled(Long orderId);
    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus  orderStatus);

}
