package dev.dharam.orderservice.service;

import dev.dharam.orderservice.client.productservice.ProductServiceClient;
import dev.dharam.orderservice.client.productservice.dto.ProductDetailsDto;
import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.exception.ResourceNotFoundException;
import dev.dharam.orderservice.mapper.OrderMapper;
import dev.dharam.orderservice.model.Order;
import dev.dharam.orderservice.model.OrderItem;
import dev.dharam.orderservice.model.OrderStatus;
import dev.dharam.orderservice.model.PaymentStatus;
import dev.dharam.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;


    @Override
    @Transactional
    public OrderResponseDto createOrder(UUID userId, OrderRequestDto requestDto) {
        Order order = OrderMapper.toEntity(requestDto);

        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        //get from productServiceClient to calculate total and use here
        Double totalAmount =0.0;
        for(OrderItem orderItem: order.getItems()){
            ProductDetailsDto productInfo = productServiceClient.getProductDetails(orderItem.getProductId());

            if(productInfo.availableQuantity() < orderItem.getQuantity()){
                throw new RuntimeException("Product '" + productInfo.title() + "' out of stock!");
            }

            //reduce stock from productService
            productServiceClient.reduceStock(orderItem.getProductId(), orderItem.getQuantity());

            orderItem.setProductName(productInfo.title());
            orderItem.setPrice(productInfo.price());

            totalAmount += orderItem.getPrice() * orderItem.getQuantity();

        }
        order.setTotalAmount(totalAmount);

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
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, UUID userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order with id: " + orderId + " not found!")
        );
        if(!order.getUserId().equals(userId)){
            throw new ResourceNotFoundException("Could not cancel the order with id: " + orderId);
        }

        markAsCancelled(orderId);

        return OrderMapper.toResponseDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrdersByUserId(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderMapper::toResponseDto)
                .toList();

    }

    @Override
    @Transactional
    public void markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                ()-> new ResourceNotFoundException("Order with id: " + orderId + " not found!")
        );

        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void markAsCancelled(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                ()-> new ResourceNotFoundException("Order with id: " + orderId + " not found!")
        );

        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        orderRepository.save(order);

        //call to productServiceClient to increase the stock
        for(OrderItem orderItem: order.getItems()){
            productServiceClient.increaseStock(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order ID " + orderId + " nahi mila!"));

        // 1. Common Blocking Logic
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot change status of a " + order.getStatus() + " order.");
        }

        // 2. Specific Status Transitions (Smart Logic)
        switch (newStatus) {
            case SHIPPED -> {
                // if only CONFIRMED can be SHIPPED
                if (order.getStatus() != OrderStatus.CONFIRMED) {
                    throw new IllegalArgumentException("Order must be CONFIRMED before shipping!");
                }
                log.info("Order {} is now out for shipping.", orderId);
            }

            case DELIVERED -> {
                // If payment is PENDING (in case of COD),
                if (order.getPaymentStatus() == PaymentStatus.PENDING) {
                    order.setPaymentStatus(PaymentStatus.COMPLETED);
                    log.info("COD Order {} payment received on delivery.", orderId);
                }
                log.info("Order {} delivered successfully.", orderId);
            }

            case RETURNED -> {
                // On return, again call to productServiceClient to increase stock
                for (OrderItem item : order.getItems()) {
                    productServiceClient.increaseStock(item.getProductId(), item.getQuantity());
                }
                log.info("Order {} returned. Stock restored.", orderId);
            }

            case CANCELLED -> {
                return cancelOrder(orderId, order.getUserId());
            }
        }

        // 3. Final Save
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return OrderMapper.toResponseDto(updatedOrder);
    }



}


