package dev.dharam.orderservice.mapper;

import dev.dharam.orderservice.dto.OrderItemRequestDto;
import dev.dharam.orderservice.dto.OrderItemResponseDto;
import dev.dharam.orderservice.dto.OrderRequestDto;
import dev.dharam.orderservice.dto.OrderResponseDto;
import dev.dharam.orderservice.model.Order;
import dev.dharam.orderservice.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    // 1. Order (Entity) -> OrderResponseDto (Record)
    public static OrderResponseDto toResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(OrderMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt(),
                itemDtos
        );
    }

    // 2. OrderItem (Entity) -> OrderItemResponseDto (Record)
    public static OrderItemResponseDto toItemResponseDto(OrderItem item) {
        return new OrderItemResponseDto(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice()
        );
    }

    // 3. OrderRequestDto (Record) -> Order (Entity)
    public static Order toEntity(OrderRequestDto requestDto) {
        Order order = new Order();
        // Note: userId -> we get from token in controller

        if (requestDto.items() != null) {
            List<OrderItem> items = requestDto.items().stream()
                    .map(itemDto -> toItemEntity(itemDto, order))
                    .collect(Collectors.toList());
            order.setItems(items);
        }
        return order;
    }

    // 4. OrderItemRequestDto (Record) -> OrderItem (Entity)
    private static OrderItem toItemEntity(OrderItemRequestDto itemDto, Order order) {
        OrderItem item = new OrderItem();
        item.setProductId(itemDto.productId());
        item.setQuantity(itemDto.quantity());
        item.setOrder(order); // Parent reference set karna zaroori hai
        // Note: productName aur price hum ProductService call ke baad set karenge
        return item;
    }
}