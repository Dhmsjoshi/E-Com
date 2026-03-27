package dev.dharam.cartservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseAuditModel {

    @Id
    private UUID userId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "cart_user_id")
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    private Double totalAmount;

    public void updateTotalAmount() {
        this.totalAmount = (cartItems == null || cartItems.isEmpty())?0.0:
                cartItems.stream()
                        .mapToDouble(CartItem::getItemTotal)
                        .sum();
    }
}
