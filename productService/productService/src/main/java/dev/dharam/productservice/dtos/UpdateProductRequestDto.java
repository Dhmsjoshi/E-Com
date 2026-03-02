package dev.dharam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequestDto {
    private double price;
    private String description;
    private String imageUrl;
}
