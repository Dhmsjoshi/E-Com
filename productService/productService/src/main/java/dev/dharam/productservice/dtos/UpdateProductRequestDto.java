package dev.dharam.productservice.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;


public record UpdateProductRequestDto (
    @PositiveOrZero(message = "Price can not be negative!")
    Double price,

    @Size(min = 5, max = 200, message = "Name must be between 5 and 200 characters")
    String description,

    @URL(message = "Please provide a valid image URL")
    String imageUrl
){};
