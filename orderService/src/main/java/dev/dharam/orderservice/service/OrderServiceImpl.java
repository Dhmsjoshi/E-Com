package dev.dharam.orderservice.service;

import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.exception.ResourceNotFoundException;
import dev.dharam.orderservice.mapper.OrderMapper;
import dev.dharam.orderservice.model.Order;
import dev.dharam.orderservice.model.OrderStatus;
import dev.dharam.orderservice.model.PaymentStatus;
import dev.dharam.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDto createOrder(UUID userId, OrderRequestDto requestDto) {
        Order order = OrderMapper.toEntity(requestDto);

        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        //get from product service to calculate total and use here
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toResponseDto(savedOrder);
    }

    @Override
    public OrderResponseDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order with id: " + orderId + " not found!")
        );
        return OrderMapper.toResponseDto(order);
    }

    @Override
    public OrderResponseDto cancelOrder(Long orderId, UUID userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order with id: " + orderId + " not found!")
        );
        if(!order.getUserId().equals(userId)){
            throw new ResourceNotFoundException("Could not cancel the order with id: " + orderId);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);

        Order canceledOrder = orderRepository.save(order);
        return OrderMapper.toResponseDto(canceledOrder);
    }

    @Override
    public List<OrderResponseDto> getOrdersByUserId(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderMapper::toResponseDto)
                .toList();

    }
}
