package dev.dharam.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem extends BaseAuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false) // Foreign Key naming
    private Order order;

    @Column(name = "product_id", nullable = false)
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Column(name = "product_name") // Snapshot of product name
    private String productName;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(nullable = false)
    @DecimalMin(value = "0.0")
    private Double price; // Price at the time of order
}