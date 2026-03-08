package dev.dharam.productservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.URL;


public record CreateProductRequestDto (
    @NotBlank(message = "Product title can not be empty!")
    @Size(min = 3, max = 20, message = "Product title must be between 3 and 20 characters")
    String title,

    @NotBlank(message = "Price can not be empty!")
    @PositiveOrZero(message = "Price can not be negative!")
    Double price,

    @NotBlank(message = "Description can not be empty!")
    @Size(min = 5, max = 200, message = "Name must be between 5 and 200 characters")
    String description,

    @NotBlank
    Long categoryId,

    @URL(message = "Please provide a valid image URL")
    String imageUrl
){};
