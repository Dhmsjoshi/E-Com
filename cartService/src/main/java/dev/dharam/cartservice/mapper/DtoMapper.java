package dev.dharam.cartservice.mapper;

import dev.dharam.cartservice.dto.CartItemResponseDto;
import dev.dharam.cartservice.dto.CartResponseDto;
import dev.dharam.cartservice.model.Cart;
import dev.dharam.cartservice.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DtoMapper {

    public static CartResponseDto mapToCartResponseDto(Cart cart) {
        if(cart == null) return null;

        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
                .map(item->
                        new CartItemResponseDto(
                                item.getId(),
                                item.getProductid(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getItemTotal()
                        )).toList();

        return new CartResponseDto(cart.getUserId(), itemDtos, cart.getTotalAmount());
    }
}
