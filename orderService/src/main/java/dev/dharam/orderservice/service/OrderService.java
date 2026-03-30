package dev.dharam.orderservice.service;

import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponseDto createOrder(UUID userId, OrderRequestDto requestDto);
    OrderResponseDto getOrderDetails(Long orderId);
    OrderResponseDto cancelOrder(Long orderId, UUID userId);
    List<OrderResponseDto> getOrdersByUserId(UUID userId);

}
