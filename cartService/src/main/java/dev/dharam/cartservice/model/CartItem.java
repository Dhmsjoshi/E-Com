package dev.dharam.cartservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseAuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productid;

    private Double price;
    private Integer quantity;

    public Double getItemTotal(){
        return (this.price!= null && this.quantity != null && this.price > 0.0) ? this.price * this.quantity :0.0;
    }
}
