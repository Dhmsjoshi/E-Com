package dev.dharam.cartservice.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
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
    @Nonnull
    private Long productid;

    @Column(name = "product_name")
    private String productName;

    @PositiveOrZero
    private Double price;
    @Min(1)
    private Integer quantity;

    public Double getItemTotal(){
        return (this.price!= null && this.quantity != null && this.price > 0.0) ? this.price * this.quantity :0.0;
    }
}
