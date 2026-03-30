package dev.dharam.productservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel {
    @Column(nullable = false, unique = true, length = 100)
    private String title;

    @Column(nullable = false)
    @Positive(message = "Price must be positive")
    private double price;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @Column(length = 1024)
    private String imageUrl;

    @Column(nullable = false)
    @Min(value = 0, message = "Stock cannot be negative")
    private int quantity; // to track inventory

    @Version
    private Long version; // for Optimistic Locking

}
