package dev.dharam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateProductRequestDto {
    private String title;
    private double price;
    private String description;
    private String category;
    private String imageUrl;
}
